import express from 'express';
import { supabase } from '../supabaseClient.js';
import { requireAuth } from '../middleware/auth.js';
const router = express.Router();

router.get('/', requireAuth, async (req,res,next)=>{
  try{
    const { data } = await supabase.from('notifications').select('*').eq('user_id', req.user.id).order('created_at',{ascending:false}).limit(50);
    res.json(data||[]);
  }catch(e){next(e);}
});
export default router;
