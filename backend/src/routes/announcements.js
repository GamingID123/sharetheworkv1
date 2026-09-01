import express from 'express';
import { supabase } from '../supabaseClient.js';
import { requireAuth, requireRole } from '../middleware/auth.js';
const router = express.Router();

router.get('/', requireAuth, async (req,res,next)=>{
  try{
    const { data } = await supabase.from('announcements').select('*').order('created_at',{ascending:false}).limit(50);
    res.json((data||[]).map(r=>({ id:r.id, title:r.title, description:r.description, date:r.created_at, author:r.author_name, targetClass:r.target_class, targetSection:r.target_section, isImportant:r.is_important })));
  }catch(e){next(e);}
});
router.post('/', requireAuth, requireRole('MODERATOR','ADMIN'), async (req,res,next)=>{
  try{
    const { data } = await supabase.from('announcements').insert({ title:req.body.title, description:req.body.description, author_id:req.user.id, author_name:req.user.email, target_class:req.body.targetClass, target_section:req.body.targetSection, is_important:req.body.isImportant||false }).select().single();
    res.status(201).json(data);
  }catch(e){next(e);}
});
export default router;
