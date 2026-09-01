import express from 'express';
import multer from 'multer';
import { requireAuth } from '../middleware/auth.js';
import { uploadBuffer, getFileMeta, streamFile, downloadFile, backupJsonToFirebase } from '../services/firebase.js';

const router = express.Router();
const upload = multer({
  storage: multer.memoryStorage(),
  limits: { fileSize: (parseInt(process.env.MAX_FILE_SIZE_MB) || 10) * 1024 * 1024 },
  fileFilter: (req, file, cb) => {
    if (file.mimetype.includes('executable') || file.originalname.match(/\.(exe|sh|bat)$/i)) return cb(new Error('File type not allowed'));
    cb(null, true);
  }
});

// Upload -> Firebase Storage (replaces Google Drive). Folder param maps to storage prefix.
router.post('/upload', requireAuth, upload.single('file'), async (req, res, next) => {
  try {
    if (!req.file) return res.status(400).json({ error: 'No file provided' });
    const folder = req.body.folder || req.query.folder || 'sharethework';
    const result = await uploadBuffer({ buffer: req.file.buffer, fileName: req.file.originalname, mimeType: req.file.mimetype, folderName: folder });
    res.json({
      fileId: result.fileId,
      fileName: req.file.originalname,
      mimeType: req.file.mimetype,
      size: req.file.size,
      storagePath: result.storagePath,
      previewUrl: `/api/drive/files/${result.fileId}/preview`,
      downloadUrl: `/api/drive/files/${result.fileId}/download`,
      // alias for new clients
      previewUrlStorage: `/api/storage/files/${result.fileId}/preview`,
      downloadUrlStorage: `/api/storage/files/${result.fileId}/download`
    });
  } catch (e) { next(e); }
});

router.get('/files/:id', requireAuth, async (req, res, next) => {
  try {
    const meta = await getFileMeta(req.params.id);
    res.json({ ...meta, previewUrl: `/api/drive/files/${meta.id}/preview`, downloadUrl: `/api/drive/files/${meta.id}/download` });
  } catch (e) { next(e); }
});

router.get('/files/:id/preview', requireAuth, async (req, res, next) => {
  try { await streamFile(req.params.id, res); } catch (e) { next(e); }
});

router.get('/files/:id/download', requireAuth, async (req, res, next) => {
  try { await downloadFile(req.params.id, res); } catch (e) { next(e); }
});

// Backup JSON (messages/homework/AI etc) -> Firebase Storage
router.post('/backup', requireAuth, async (req, res, next) => {
  try {
    const { path, data } = req.body;
    if (!path || !data) return res.status(400).json({ error: 'path and data required' });
    const fileId = await backupJsonToFirebase(path, data);
    res.json({ fileId, path });
  } catch (e) { next(e); }
});

export default router;
