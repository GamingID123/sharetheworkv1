package com.sharethework.data.remote

object Config {
    // Vercel prod — classmates hit this; for local emulator use http://10.0.2.2:3000/api/
    const val API_BASE_URL = "https://backendsharetheworkappappwrkback-4gca1rpkr.vercel.app/api/"
    const val SUPABASE_URL = "https://gatkaergcdnuxwzocupu.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdhdGthZXJnY2RudXh3em9jdXB1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzk5Mzc1ODYsImV4cCI6MjA5NTUxMzU4Nn0.KHiPIKWuRVOuIrYJLpv4bstfDHasqgKy77YsIIj586s"
    const val MAX_FILE_SIZE_MB = 10
    const val DRIVE_PREVIEW_PATH = "drive/files/"
    const val STORAGE_PREVIEW_PATH = "storage/files/"
}
