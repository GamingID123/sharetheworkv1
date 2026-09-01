import express from 'express';
import { supabase } from '../supabaseClient.js';
import { requireAuth, requireRole } from '../middleware/auth.js';
const router = express.Router();


router.get('/', requireAuth, async (req,res,next)=>{
  try{
    let q = supabase.from('classwork').select('*').order('created_at', { ascending:false });
    const { class: c, section: s, q: search } = req.query;
    if(c) q=q.eq('class_name',c); else if(req.user.role==='STUDENT') q=q.eq('class_name', req.user.className);
    if(s) q=q.eq('section',s); else if(req.user.role==='STUDENT') q=q.eq('section', req.user.section);
    if(search) q=q.ilike('title', `%${search}%`);
    const { data } = await q;
    res.json((data||[]).map(r=>({ id:r.id, subject:r.subject, title:r.title, description:r.description, className:r.class_name, section:r.section, date:r.created_at, teacherName:r.teacher_name })));
  }catch(e){next(e);}
});
router.post('/', requireAuth, requireRole('MODERATOR','ADMIN'), async (req,res,next)=>{
  try{
    const { data } = await supabase.from('classwork').insert({ subject:req.body.subject, title:req.body.title, description:req.body.description, class_name:req.body.className, section:req.body.section, teacher_id:req.user.id, teacher_name:req.user.email }).select().single();
    res.status(201).json(data);
  }catch(e){next(e);}
});
export default router;
