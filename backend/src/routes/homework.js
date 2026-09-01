import express from 'express';
import Joi from 'joi';
import { supabase } from '../supabaseClient.js';
import { requireAuth, requireRole } from '../middleware/auth.js';

const router = express.Router();

const schema = Joi.object({
  subject: Joi.string().required(), title: Joi.string().required(), description: Joi.string().allow(''),
  className: Joi.string().required(), section: Joi.string().required(), dueDate: Joi.string().isoDate().required()
});

// Student/any auth can read, but filtered by class/section unless admin
router.get('/', requireAuth, async (req,res,next)=>{
  try{
    let q = supabase.from('homework').select('*').order('due_date', { ascending: true });
    const { class: c, section: s, subject, q: search } = req.query;
    if (c) q = q.eq('class_name', c);
    else if (req.user.role === 'STUDENT') q = q.eq('class_name', req.user.className);
    if (s) q = q.eq('section', s);
    else if (req.user.role === 'STUDENT') q = q.eq('section', req.user.section);
    if (subject) q = q.eq('subject', subject);
    if (search) q = q.ilike('title', `%${search}%`);
    const { data } = await q;
    res.json((data||[]).map(mapHw));
  }catch(e){ next(e); }
});

router.post('/', requireAuth, requireRole('MODERATOR','ADMIN'), async (req,res,next)=>{
  try{
    const { error, value } = schema.validate(req.body);
    if(error) return res.status(400).json({ error:error.message });
    // Moderator scoped to own class
    if(req.user.role==='MODERATOR' && (value.className!==req.user.className || value.section!==req.user.section))
      return res.status(403).json({ error:'Moderator can only post for assigned class/section' });
    const { data, error: dbErr } = await supabase.from('homework').insert({
      subject: value.subject, title: value.title, description: value.description,
      class_name: value.className, section: value.section, due_date: value.dueDate,
      teacher_id: req.user.id, teacher_name: req.user.email
    }).select().single();
    if(dbErr) throw dbErr;
    res.status(201).json(mapHw(data));
  }catch(e){ next(e); }
});

router.put('/:id', requireAuth, requireRole('MODERATOR','ADMIN'), async (req,res,next)=>{
  try{
    const { data: existing } = await supabase.from('homework').select('*').eq('id', req.params.id).single();
    if(!existing) return res.status(404).json({ error:'Not found' });
    if(req.user.role==='MODERATOR' && existing.teacher_id!==req.user.id) return res.status(403).json({ error:'Not owner' });
    const { data } = await supabase.from('homework').update({ title:req.body.title, description:req.body.description }).eq('id', req.params.id).select().single();
    res.json(mapHw(data));
  }catch(e){ next(e); }
});

router.delete('/:id', requireAuth, requireRole('MODERATOR','ADMIN'), async (req,res,next)=>{
  try{
    const { data: existing } = await supabase.from('homework').select('teacher_id').eq('id', req.params.id).single();
    if(req.user.role==='MODERATOR' && existing?.teacher_id!==req.user.id) return res.status(403).json({ error:'Not owner' });
    await supabase.from('homework').delete().eq('id', req.params.id);
    res.json({ message:'Deleted' });
  }catch(e){ next(e); }
});

router.post('/:id/complete', requireAuth, async (req,res,next)=>{
  try{
    await supabase.from('homework_completions').upsert({ homework_id:req.params.id, user_id:req.user.id, status:'COMPLETED' });
    res.json({ message:'Marked completed' });
  }catch(e){ next(e); }
});

function mapHw(r){ return { id:r.id, subject:r.subject, title:r.title, description:r.description, className:r.class_name, section:r.section, dateAssigned:r.created_at, dueDate:r.due_date, teacherName:r.teacher_name, teacherId:r.teacher_id, attachments:[] }; }

export default router;
