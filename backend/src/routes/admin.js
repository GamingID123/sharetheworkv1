import express from 'express';
import { supabase } from '../supabaseClient.js';
import { requireAuth, requireRole } from '../middleware/auth.js';
const router = express.Router();


router.get('/stats', requireAuth, requireRole('ADMIN'), async (req,res,next)=>{
  try{
    const { count: users } = await supabase.from('users').select('*', { count:'exact', head:true });
    const { count: hw } = await supabase.from('homework').select('*', { count:'exact', head:true });
    const { count: msgs } = await supabase.from('messages').select('*', { count:'exact', head:true });
    const { count: reports } = await supabase.from('reports').select('*', { count:'exact', head:true });
    res.json({ totalUsers: users||0, homeworkUploaded: hw||0, messagesSent: msgs||0, reports: reports||0 });
  }catch(e){next(e);}
});
router.get('/users', requireAuth, requireRole('ADMIN'), async (req,res,next)=>{
  try{
    let q = supabase.from('users').select('*');
    if(req.query.q) q=q.ilike('name', `%${req.query.q}%`);
    const { data } = await q.limit(50);
    res.json((data||[]).map(u=>({ id:u.id, name:u.name, email:u.email, className:u.class_name, section:u.section, role:u.role, status:u.status, joinDate:u.created_at })));
  }catch(e){next(e);}
});
router.put('/users/:id/role', requireAuth, requireRole('ADMIN'), async (req,res,next)=>{
  try{
    const { data } = await supabase.from('users').update({ role:req.body.role }).eq('id', req.params.id).select().single();
    res.json(data);
  }catch(e){next(e);}
});
router.put('/users/:id/status', requireAuth, requireRole('ADMIN'), async (req,res,next)=>{
  try{
    const { data } = await supabase.from('users').update({ status:req.body.status }).eq('id', req.params.id).select().single();
    res.json(data);
  }catch(e){next(e);}
});
router.get('/reports', requireAuth, requireRole('ADMIN','MODERATOR'), async (req,res,next)=>{
  try{
    const { data } = await supabase.from('reports').select('*').order('created_at',{ascending:false});
    res.json(data||[]);
  }catch(e){next(e);}
});
export default router;
