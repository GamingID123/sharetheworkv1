import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import morgan from 'morgan';
import dotenv from 'dotenv';
import rateLimit from 'express-rate-limit';
import authRoutes from './routes/auth.js';
import homeworkRoutes from './routes/homework.js';
import classworkRoutes from './routes/classwork.js';
import announcementRoutes from './routes/announcements.js';
import chatRoutes from './routes/chat.js';
import aiRoutes from './routes/ai.js';
import adminRoutes from './routes/admin.js';
import notificationRoutes from './routes/notifications.js';
import driveRoutes from './routes/drive.js';
import storageRoutes from './routes/storage.js';

dotenv.config();
const app = express();
const PORT = process.env.PORT || 3000;

app.use(helmet());
app.use(cors({ origin: process.env.CORS_ORIGIN || '*' }));
app.use(express.json({ limit: '10mb' }));
app.use(morgan('dev'));

const limiter = rateLimit({ windowMs: 60_000, max: 60, message: { error: 'Too many requests' } });
app.use('/api/', limiter);

app.get('/health', (req, res) => res.json({ status: 'ok', app: 'ShareTheWork', time: new Date().toISOString() }));

app.use('/api/auth', authRoutes);
app.use('/api/homework', homeworkRoutes);
app.use('/api/classwork', classworkRoutes);
app.use('/api/announcements', announcementRoutes);
app.use('/api/conversations', chatRoutes);
app.use('/api/messages', chatRoutes);
app.use('/api/ai', aiRoutes);
app.use('/api/admin', adminRoutes);
app.use('/api/notifications', notificationRoutes);
app.use('/api/drive', driveRoutes);
app.use('/api/storage', storageRoutes); // Firebase alias (same handler, new preferred path)

// 404
app.use((req, res) => res.status(404).json({ error: 'Not found' }));
// error handler
app.use((err, req, res, next) => {
  console.error(err);
  res.status(err.status || 500).json({ error: err.message || 'Internal error' });
});

if (process.env.VERCEL !== '1') {
  app.listen(PORT, () => console.log(`ShareTheWork backend running on http://localhost:${PORT}`));
}
export default app;
