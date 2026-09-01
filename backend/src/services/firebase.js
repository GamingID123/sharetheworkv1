import admin from 'firebase-admin';
import { DEFAULTS, env } from '../defaults.js';

// Firebase Admin - used for ALL file + data storage (replaces Google Drive)
// Env: FIREBASE_SERVICE_ACCOUNT_JSON (full service account JSON or base64), FIREBASE_STORAGE_BUCKET
// Falls back to DEFAULTS.FIREBASE_SERVICE_ACCOUNT so app works without manual .env

let app = null;
let bucket = null;

function getAdmin() {
  if (app && bucket) return { app, bucket };
  if (admin.apps.length > 0) {
    app = admin.apps[0];
    bucket = admin.storage().bucket();
    return { app, bucket };
  }
  const bucketName = env('FIREBASE_STORAGE_BUCKET', DEFAULTS.FIREBASE_STORAGE_BUCKET);
  let raw = process.env.FIREBASE_SERVICE_ACCOUNT_JSON || process.env.GOOGLE_APPLICATION_CREDENTIALS_JSON;
  let creds = null;
  if (raw) {
    try { creds = raw.trim().startsWith('{') ? JSON.parse(raw) : JSON.parse(Buffer.from(raw, 'base64').toString('utf8')); } catch {}
  }
  if (!creds) creds = DEFAULTS.FIREBASE_SERVICE_ACCOUNT;
  if (!creds || !bucketName) {
    console.warn('[Firebase] FIREBASE_SERVICE_ACCOUNT_JSON or FIREBASE_STORAGE_BUCKET not set - Firebase Storage disabled');
    return { app: null, bucket: null };
  }
  try {
    if (creds.private_key) creds.private_key = creds.private_key.replace(/\\n/g, '\n');
    app = admin.initializeApp({
      credential: admin.credential.cert(creds),
      storageBucket: bucketName
    });
    bucket = admin.storage().bucket();
    console.log('[Firebase] initialized bucket:', bucketName);
    return { app, bucket };
  } catch (e) {
    console.error('[Firebase] init failed', e.message);
    return { app: null, bucket: null };
  }
}

function safeFileName(name) { return name.replace(/[^a-zA-Z0-9._-]/g, '_'); }

export async function uploadBuffer({ buffer, fileName, mimeType, folderName }) {
  const { bucket: b } = getAdmin();
  if (!b) throw new Error('Firebase not configured - set FIREBASE_SERVICE_ACCOUNT_JSON and FIREBASE_STORAGE_BUCKET');
  const folder = (folderName || 'sharethework').replace(/^\/+|\/+$/g, '');
  const key = `${folder}/${Date.now()}_${safeFileName(fileName)}`;
  const file = b.file(key);
  await file.save(buffer, { contentType: mimeType, resumable: false, metadata: { contentType: mimeType } });
  // Make preview easy via backend proxy (no need for signed URL); also generate signed URL for direct preview if needed
  const [signedUrl] = await file.getSignedUrl({ action: 'read', expires: Date.now() + 7 * 24 * 60 * 60 * 1000 }).catch(() => [null]);
  return {
    fileId: encodeURIComponent(key), // used as :id in /api/drive/files/:id paths (encoded storage key)
    fileName,
    mimeType,
    size: buffer.length,
    storagePath: key,
    signedUrl,
    previewUrl: `/api/drive/files/${encodeURIComponent(key)}/preview`,
    downloadUrl: `/api/drive/files/${encodeURIComponent(key)}/download`
  };
}

export async function getFileMeta(fileId) {
  const { bucket: b } = getAdmin();
  if (!b) throw new Error('Firebase not configured');
  const key = decodeURIComponent(fileId);
  const file = b.file(key);
  const [metadata] = await file.getMetadata();
  return {
    id: fileId,
    name: metadata.name.split('/').pop(),
    mimeType: metadata.contentType,
    size: Number(metadata.size),
    storagePath: key,
    createdTime: metadata.timeCreated,
    updatedTime: metadata.updated,
    timeCreated: metadata.timeCreated
  };
}

export async function streamFile(fileId, res) {
  const { bucket: b } = getAdmin();
  if (!b) throw new Error('Firebase not configured');
  const key = decodeURIComponent(fileId);
  const file = b.file(key);
  const [metadata] = await file.getMetadata().catch(() => [{ contentType: 'application/octet-stream', name: key }]);
  const name = (metadata.name || key).split('/').pop();
  res.setHeader('Content-Type', metadata.contentType || 'application/octet-stream');
  res.setHeader('Content-Disposition', `inline; filename="${name}"`);
  res.setHeader('Cache-Control', 'private, max-age=300');
  const stream = file.createReadStream();
  return new Promise((resolve, reject) => {
    stream.on('error', reject);
    stream.on('end', resolve);
    stream.pipe(res);
  });
}

export async function downloadFile(fileId, res) {
  const { bucket: b } = getAdmin();
  if (!b) throw new Error('Firebase not configured');
  const key = decodeURIComponent(fileId);
  const file = b.file(key);
  const [metadata] = await file.getMetadata().catch(() => [{ contentType: 'application/octet-stream', name: key }]);
  const name = (metadata.name || key).split('/').pop();
  res.setHeader('Content-Type', metadata.contentType || 'application/octet-stream');
  res.setHeader('Content-Disposition', `attachment; filename="${name}"`);
  const stream = file.createReadStream();
  return new Promise((resolve, reject) => {
    stream.on('error', reject);
    stream.on('end', resolve);
    stream.pipe(res);
  });
}

// Backup arbitrary JSON (messages / homework / AI etc) to Firebase Storage as {path}.json
export async function backupJsonToFirebase(relativePath, jsonData) {
  const { bucket: b } = getAdmin();
  if (!b) return null;
  const key = relativePath.replace(/^\/+/, '');
  const file = b.file(key);
  await file.save(Buffer.from(JSON.stringify(jsonData, null, 2), 'utf8'), { contentType: 'application/json', resumable: false });
  return encodeURIComponent(key);
}

// Firestore helper (optional, if you also want structured data)
export function getFirestore() {
  const { app: a } = getAdmin();
  if (!a) return null;
  return admin.firestore();
}
