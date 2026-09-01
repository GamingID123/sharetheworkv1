import jwt from 'jsonwebtoken';
import { DEFAULTS, env } from '../defaults.js';

export function requireAuth(req, res, next) {
  // Allow Bearer header OR ?token= query (needed for DownloadManager/WebView which can't set headers)
  let token = null;
  const header = req.headers.authorization;
  if (header?.startsWith('Bearer ')) token = header.slice(7);
  else if (req.query?.token) token = req.query.token;
  else if (req.headers['x-access-token']) token = req.headers['x-access-token'];
  if (!token) return res.status(401).json({ error: 'Missing token' });
  try {
    const payload = jwt.verify(token, env('JWT_SECRET', 'dev-jwt-secret-32chars-sharethework-demo'));
    req.user = payload;
    next();
  } catch {
    return res.status(401).json({ error: 'Invalid token' });
  }
}

export function requireRole(...roles) {
  return (req, res, next) => {
    if (!req.user || !roles.includes(req.user.role)) return res.status(403).json({ error: 'Forbidden' });
    next();
  };
}

// Basic in-memory profanity / harassment filter (extend with real ML service)
const BLOCKED = ['abuse','hate','kill','spam'];
export function moderateText(text) {
  if (!text) return { ok: true };
  const lower = text.toLowerCase();
  for (const w of BLOCKED) if (lower.includes(w)) return { ok: false, reason: `Contains blocked word: ${w}` };
  return { ok: true };
}
