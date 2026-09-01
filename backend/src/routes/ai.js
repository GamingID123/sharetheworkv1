import express from 'express';
import { requireAuth } from '../middleware/auth.js';
import { createClient } from '@supabase/supabase-js';
import { DEFAULTS, env } from '../defaults.js';
const supaUrl = env('SUPABASE_URL', DEFAULTS.SUPABASE_URL);
const supaService = env('SUPABASE_SERVICE_ROLE_KEY', DEFAULTS.SUPABASE_SERVICE_ROLE_KEY);
const router = express.Router();
const supabase = createClient(supaUrl, supaService);

// Nova AI via Groq - server-side proxy (never expose GROQ_API_KEY to client)
// Groq OpenAI-compatible endpoint: https://api.groq.com/openai/v1/chat/completions
router.post('/chat', requireAuth, async (req, res, next) => {
  try {
    const { message, conversationId, stream } = req.body;
    if (!message || !message.trim()) return res.status(400).json({ error: 'Missing message' });

    const groqKey = env('GROQ_API_KEY', DEFAULTS.GROQ_API_KEY);
    if (!groqKey) return res.status(500).json({ error: 'GROQ_API_KEY not configured on server' });

    const groqModel = env('GROQ_MODEL', DEFAULTS.GROQ_MODEL);
    const groqBase = env('GROQ_BASE_URL', DEFAULTS.GROQ_BASE_URL);

    // Persist user message
    let convId = conversationId;
    if (!convId) {
      const { data: conv, error } = await supabase.from('ai_conversations').insert({ user_id: req.user.id, title: message.slice(0, 48) }).select().single();
      if (error) throw error;
      convId = conv.id;
    }
    await supabase.from('ai_messages').insert({ conversation_id: convId, role: 'user', content: message });

    // Fetch recent history for context (last 12 messages)
    const { data: history } = await supabase.from('ai_messages').select('role,content').eq('conversation_id', convId).order('created_at', { ascending: true }).limit(12);
    const messages = [
      { role: 'system', content: 'You are Nova, ShareTheWork AI study assistant. Help students understand homework, explain step-by-step, summarize notes, generate quizzes, and revise. Be concise, friendly, and never do homework for them entirely — guide learning.' },
      ...(history || []).map(m => ({ role: m.role, content: m.content }))
    ];

    const groqRes = await fetch(`${groqBase}/chat/completions`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${groqKey}`, 'Content-Type': 'application/json' },
      body: JSON.stringify({
        model: groqModel,
        messages,
        temperature: 0.7,
        max_tokens: 900,
        stream: false
      })
    });

    if (!groqRes.ok) {
      const errText = await groqRes.text();
      console.error('Groq error:', groqRes.status, errText);
      return res.status(502).json({ error: 'Groq API error', details: errText.slice(0, 500) });
    }

    const data = await groqRes.json();
    const reply = data.choices?.[0]?.message?.content || 'Nova is thinking... try again.';

    await supabase.from('ai_messages').insert({ conversation_id: convId, role: 'assistant', content: reply });

    // Also backup conversation to Firebase Storage if enabled (async, non-blocking)
    if (process.env.FIREBASE_BACKUP_ENABLED === 'true') {
      import('../services/firebase.js').then(m => m.backupJsonToFirebase(`nova/${req.user.id}/${convId}.json`, { conversationId: convId, messages: [...messages, { role:'assistant', content: reply }] }).catch(()=>{}));
    }

    res.json({ conversationId: convId, reply, model: groqModel, usage: data.usage || null });
  } catch (e) { next(e); }
});

router.get('/conversations', requireAuth, async (req, res, next) => {
  try {
    const { data } = await supabase.from('ai_conversations').select('*').eq('user_id', req.user.id).order('created_at', { ascending: false });
    res.json(data || []);
  } catch (e) { next(e); }
});

router.get('/conversations/:id', requireAuth, async (req, res, next) => {
  try {
    const { data } = await supabase.from('ai_messages').select('*').eq('conversation_id', req.params.id).order('created_at', { ascending: true });
    res.json(data || []);
  } catch (e) { next(e); }
});

export default router;
