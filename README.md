# ShareTheWork — Learn. Share. Connect.

Private school community app combining **Homework + Classwork + Student Communication + Nova AI** in one premium black-and-white platform.

![Platform](https://img.shields.io/badge/platform-Android-black)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-white)
![Backend](https://img.shields.io/badge/backend-Node%20%2B%20Express%20%2B%20Supabase-black)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

---

## Features
- **Auth** — Sign up/login/logout, password reset, profile edit, role-based access (Student/Moderator/Admin), domain restriction
- **Home** — Personalized dashboard with today's homework, classwork, announcements, unread messages, AI shortcut
- **Homework & Classwork** — CRUD by subject/class/section/date, attachments (PDF/images), search/filter, submission tracking
- **Chat** — Private, community & class-group realtime messaging, replies, deletes, typing/online indicators, notifications
- **Moderation** — Auto keyword filter + manual reports, hide/delete/warn/restrict/suspend, admin moderation queue
- **Nova AI (Groq)** — Groq-powered (`llama-3.3-70b-versatile` via `https://api.groq.com/openai/v1/chat/completions`), homework help, summaries, quizzes, step-by-step, server-side proxy — `GROQ_API_KEY` never in APK
- **Firebase Storage** — All attachments + messages/other data stored in Firebase Storage via `firebase-admin` (`/api/storage/*` + legacy `/api/drive/*` alias). Bucket `FIREBASE_STORAGE_BUCKET`; preview/download streamed via `FilePreviewScreen`.
- **In-app Preview & Download** — `FilePreviewScreen` renders images via Coil (`/api/storage/files/{id}/preview`) and PDFs via WebView + Google Docs gview (`inline` disposition); Download via `DownloadManager` → `Downloads/` + `Content-Disposition: attachment` (`/api/storage/files/{id}/download`) with share. No external app needed.
- **Announcements** — Targeted by class/section, highlighted important posts
- **Notifications** — In-app + FCM push for homework/classwork/announcements/messages
- **Admin & Moderator Dashboards** — User management, content moderation, stats
- **Offline** — Room caching for homework/classwork/announcements with offline banner + sync

## Design System
Black/dark background `#0A0A0A`, white text, grey cards `#1A1A1A`, soft glow on CTAs, glassmorphism, rounded corners (16-24dp), smooth transitions, responsive layouts.

Bottom nav: Home | Homework | Chat | AI | Profile

## Project Structure
```
sharethework/
├── android/                # Android app (Kotlin + Compose)
│   ├── app/
│   │   ├── build.gradle.kts
│   │   └── src/main/
│   │       ├── AndroidManifest.xml
│   │       ├── java/com/sharethework/
│   │       └── res/
│   ├── build.gradle.kts
│   └── settings.gradle.kts
├── backend/                # Express API + Supabase
│   ├── src/
│   │   ├── index.js
│   │   ├── routes/
│   │   └── middleware/
│   └── package.json
├── supabase/
│   ├── schema.sql
│   └── seed.sql
├── .env.example
└── README.md
```

## Prerequisites
- Android Studio Hedgehog+ | JDK 17 | Gradle 8.x
- Node.js 18+ | Supabase project | Firebase project (FCM)

## Setup

### 1. Clone & env
```bash
git clone https://github.com/your-org/sharethework.git
cd sharethework
cp .env.example .env
cp backend/.env.example backend/.env
# fill with your Supabase/Firebase/AI keys
```

### 2. Backend
```bash
cd backend
npm install
npm run dev        # http://localhost:3000
npm run migrate    # applies supabase/schema.sql
```

### 3. Android
1. Open `android/` in Android Studio
2. Set `local.properties` → `sdk.dir=/path/to/Android/Sdk`
3. Add `app/src/main/java/com/sharethework/data/remote/Config.kt` (generated from env, see below)
4. Sync Gradle → Run on device/emulator

**Config.kt**
```kotlin
object Config {
    const val API_BASE_URL = "http://10.0.2.2:3000/api/" // emulator loopback
    const val SUPABASE_URL = "https://your-project.supabase.co"
    const val SUPABASE_ANON_KEY = "your-anon-key"
}
```
> Never put `GROQ_API_KEY` or `SUPABASE_SERVICE_ROLE_KEY` or `FIREBASE_SERVICE_ACCOUNT_JSON` in the APK — Nova is proxied via `POST /api/ai/chat` (Groq) and files via `/api/storage/files/{id}/preview|download` (Firebase).

### 4a. Groq
- Create API key at https://console.groq.com/keys → set `GROQ_API_KEY` + `GROQ_MODEL=llama-3.3-70b-versatile`.
- Backend calls `https://api.groq.com/openai/v1/chat/completions` server-side only. Verify: `curl -H "Authorization: Bearer $GROQ_API_KEY" https://api.groq.com/openai/v1/models`.

### 4b. Firebase (Storage for messages & files, replaces Drive)
- Firebase Console → Project Settings → Service Accounts → Generate new private key → set `FIREBASE_SERVICE_ACCOUNT_JSON` (full JSON or base64) + `FIREBASE_STORAGE_BUCKET=your-project.appspot.com` + `FIREBASE_BACKUP_ENABLED=true`.
- Enable Storage, create bucket. `backend` uses `firebase-admin` (already in `package.json`). Upload test: `curl -H "Authorization: Bearer <JWT>" -F file=@test.pdf -F folder=homework http://localhost:3000/api/storage/upload` (also `/api/drive/upload` alias) → returns `previewUrl`/`downloadUrl` for `FilePreviewScreen`.

### 4. Supabase
- Create project → SQL Editor → run `supabase/schema.sql` then `seed.sql`
- Enable Auth (Email) → set allowed domains in `backend/.env`
- Create Storage bucket `sharethework-files` (public read off, RLS on)

## APK Build
```bash
cd android
./gradlew assembleDebug        # app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease      # requires signingConfig
./gradlew bundleRelease        # .aab for Play Store
```

## Database Schema
Tables: `users`, `classes`, `sections`, `homework`, `classwork`, `announcements`, `conversations`, `messages`, `groups`, `group_members`, `reports`, `notifications`, `ai_conversations`, `ai_messages`. See `supabase/schema.sql`.

## Security
- JWT auth, bcrypt passwords, role middleware, RLS policies
- Server-side validation, input sanitization, rate limiting (60 req/min)
- File type/size validation, signed storage URLs
- No secrets in APK; AI keys only on server

## Testing Checklist
- [x] Registration / Login / Logout / Password reset
- [x] Role permissions (Student/Moderator/Admin)
- [x] Homework & Classwork CRUD + filters
- [x] Announcements targeted delivery
- [x] Private & group chat + typing/online
- [x] Reports / moderation actions
- [x] Nova AI chat + history
- [x] File uploads/downloads
- [x] Search (homework/classwork/announcements/chats/users)
- [x] Offline caching + banner
- [x] Notifications

## License
MIT
