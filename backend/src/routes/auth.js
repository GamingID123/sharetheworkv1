import express from 'express';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import Joi from 'joi';
import { supabase } from '../supabaseClient.js';
import { requireAuth } from '../middleware/auth.js';
import { DEFAULTS, env } from '../defaults.js';

const router = express.Router();

const registerSchema = Joi.object({
  name: Joi.string().min(2).max(50).required(),
  email: Joi.string().email().required(),
  password: Joi.string().min(8).required(),
  className: Joi.string().required(),
  section: Joi.string().required()
});

function isAllowedDomain(email) {
  const allowed = (process.env.ALLOWED_EMAIL_DOMAINS || '').split(',').map(s=>s.trim()).filter(Boolean);
  if (allowed.length === 0) return true;
  return allowed.some(d => email.toLowerCase().endsWith('@'+d.toLowerCase()));
}

function sign(user){ return jwt.sign({ id:user.id, email:user.email, role:user.role, className:user.class_name, section:user.section }, env('JWT_SECRET', 'dev-jwt-secret-32chars-sharethework-demo'), { expiresIn: process.env.JWT_EXPIRY || '7d' }); }

router.post('/register', async (req,res,next)=>{
  try{
    const { error, value } = registerSchema.validate(req.body);
    if(error) return res.status(400).json({ error:error.message });
    if(!isAllowedDomain(value.email)) return res.status(400).json({ error:'Email domain not allowed' });
    const { data: existing } = await supabase.from('users').select('id').eq('email', value.email).single();
    if(existing) return res.status(409).json({ error:'Email already registered' });
    const hash = await bcrypt.hash(value.password, 10);
    const { data, error: dbErr } = await supabase.from('users').insert({
      name: value.name, email: value.email, password_hash: hash,
      class_name: value.className, section: value.section,
      role: 'STUDENT', status: 'ACTIVE'
    }).select().single();
    if(dbErr) throw dbErr;
    const token = sign(data);
    res.json({ token, user: sanitize(data) });
  }catch(e){ next(e); }
});

router.post('/login', async (req,res,next)=>{
  try{
    const { email, password } = req.body;
    if(!email || !password) return res.status(400).json({ error:'Missing fields' });
    const { data: user } = await supabase.from('users').select('*').eq('email', email).single();
    if(!user) return res.status(401).json({ error:'Invalid credentials' });
    if(user.status === 'SUSPENDED') return res.status(403).json({ error:'Account suspended' });
    const ok = await bcrypt.compare(password, user.password_hash);
    if(!ok) return res.status(401).json({ error:'Invalid credentials' });
    const token = sign(user);
    res.json({ token, user: sanitize(user) });
  }catch(e){ next(e); }
});

router.post('/reset-password', async (req,res,next)=>{
  try{
    const { email } = req.body;
    // In production: send email via Supabase Auth or SMTP
    res.json({ message: `If ${email} exists, reset link sent` });
  }catch(e){ next(e); }
});

router.get('/me', requireAuth, async (req,res,next)=>{
  try{
    const { data } = await supabase.from('users').select('*').eq('id', req.user.id).single();
    res.json(sanitize(data));
  }catch(e){ next(e); }
});

function sanitize(u){
  if(!u) return null;
  const { password_hash, ...rest } = u;
  return { id:rest.id, name:rest.name, email:rest.email, className:rest.class_name, section:rest.section, profilePictureUrl:rest.profile_picture_url, role:rest.role, joinDate:rest.created_at, status:rest.status };
}

export default router;
