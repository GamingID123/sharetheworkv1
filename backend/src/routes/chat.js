import express from 'express';
import { supabase } from '../supabaseClient.js';
import { requireAuth, moderateText } from '../middleware/auth.js';
const router = express.Router();


// Conversations
router.get('/conversations', requireAuth, async (req,res,next)=>{
  try{
    const { data } = await supabase.from('conversations').select('*');
    res.json(data||[]);
  }catch(e){next(e);}
});

router.get('/conversations/:id/messages', requireAuth, async (req,res,next)=>{
  try{
    const { data } = await supabase.from('messages').select('*').eq('conversation_id', req.params.id).order('created_at',{ascending:true}).limit(50);
    res.json((data||[]).map(m=>({ id:m.id, conversationId:m.conversation_id, senderId:m.sender_id, senderName:m.sender_name, text:m.is_deleted?'This message was deleted':m.text, timestamp:m.created_at, isMe:m.sender_id===req.user.id, isRead:m.is_read })));
  }catch(e){next(e);}
});

router.post('/conversations/:id/messages', requireAuth, async (req,res,next)=>{
  try{
    const { text, replyToId } = req.body;
    if(!text || !text.trim()) return res.status(400).json({ error:'Empty message' });
    const mod = moderateText(text);
    if(!mod.ok){
      // auto-hide: insert but flagged
      const { data } = await supabase.from('messages').insert({ conversation_id:req.params.id, sender_id:req.user.id, sender_name:req.user.email, text, is_hidden:true, moderation_reason:mod.reason }).select().single();
      return res.status(201).json({ ...data, warning:'Message flagged by moderation' });
    }
    const { data } = await supabase.from('messages').insert({ conversation_id:req.params.id, sender_id:req.user.id, sender_name:req.user.email, text, reply_to_id: replyToId||null }).select().single();
    // TODO: emit via Supabase Realtime + push notification
    res.status(201).json({ id:data.id, conversationId:data.conversation_id, senderId:data.sender_id, senderName:data.sender_name, text:data.text, timestamp:data.created_at, isMe:true });
  }catch(e){next(e);}
});

router.delete('/messages/:id', requireAuth, async (req,res,next)=>{
  try{
    const { data: msg } = await supabase.from('messages').select('*').eq('id', req.params.id).single();
    if(!msg || msg.sender_id!==req.user.id) return res.status(403).json({ error:'Can only delete own message' });
    await supabase.from('messages').update({ is_deleted:true, text:'[deleted]' }).eq('id', req.params.id);
    res.json({ message:'Deleted' });
  }catch(e){next(e);}
});

router.post('/messages/:id/report', requireAuth, async (req,res,next)=>{
  try{
    await supabase.from('reports').insert({ message_id:req.params.id, reported_by:req.user.id, reason:req.body.reason||'inappropriate' });
    res.json({ message:'Reported' });
  }catch(e){next(e);}
});

export default router;
