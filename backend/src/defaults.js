// ShareTheWork safe defaults — embedded so app works without manual .env
// Public: Supabase anon is safe in APK. Server secrets here are for demo/convenience only.
// For production, move these to GitHub Secrets / env and remove from repo.

export const DEFAULTS = {
  SUPABASE_URL: "https://gatkaergcdnuxwzocupu.supabase.co",
  SUPABASE_ANON_KEY: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdhdGthZXJnY2RudXh3em9jdXB1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzk5Mzc1ODYsImV4cCI6MjA5NTUxMzU4Nn0.KHiPIKWuRVOuIrYJLpv4bstfDHasqgKy77YsIIj586s",
  SUPABASE_SERVICE_ROLE_KEY: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdhdGthZXJnY2RudXh3em9jdXB1Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3OTkzNzU4NiwiZXhwIjoyMDk1NTEzNTg2fQ.e6Wfs1N6EKuN9nKS2tPhZyTqz_buZCbc0qR-bj45l8E",
  GROQ_API_KEY: "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",
  GROQ_MODEL: "llama-3.3-70b-versatile",
  GROQ_BASE_URL: "https://api.groq.com/openai/v1",
  FIREBASE_STORAGE_BUCKET: "nexcuse-50a07.appspot.com",
  // Firebase service account — keep as object so firebase.js can cert it
  FIREBASE_SERVICE_ACCOUNT: {
    type: "service_account",
    project_id: "nexcuse-50a07",
    private_key_id: "1649bc569fbac63403b100d17fac9bdb8436723c",
    private_key: "-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDDR6yRo9/6owiy\nX99mAfQKgNfpKRZRmQqBK4KaQoAQt4pHLnqJv3+dToDhqdjOpLFay3Oc/+/jUQq6\nk4cGf33lg6Ixh1KLL553vNd+4Vk1C7fCeDQnqUgVL/Uz8dKcKExJGMYhbL99x6P0\nCsVHaJMYELexZyHlSeKPTuxuOaJXoG1R5714geECus2ANHVwc4obxF5BPjSqlUAo\n0EL0EcbC1Y+ELPH70Lag5ToV4zvg8UYrXxZioM4FroPteDmVvEoMhwAYuhzPicRA\n7PQ9dkLTQseHlosrWQtyKXr3Lac+HkkNwtv1ljbdSPlBjSRvc4sRUbY58PB7kz8U\n7xUpVcurAgMBAAECggEAAf6K5O5GaZuQ1kWDyHM2DoXNu3wnorJMUncvyirqS042\nJs9ZaAVUV9AHIYbbB606tHTGDv3o8ZDdahXH0Yl4YmFeq2Ce6q+UHKC8lxEIS+VK\nCj3kOapEaePHl/3pvgRS8LiRWW9wLmgqCy8XxnvRnybLwXX0rJAXGyPETV6tHKj3\nTE78VUBn0XwXpi8GUc8DycX7u8X0ReaWA8hX54ukkMgQyJ0mSJRtLE2oLUSLOxik\naNoSxGaehJK1MxWc8pJR3toIi6M4NVCYg5WeUxk02P1TqqRDVkFjun7Bu3cdFQBr\nIwYL033tDGsbsGCKFiafqYohOTH2Fv9kCOjIuQSW9QKBgQDpGrrhPCqobzYO7QCx\nQJ+dvOlm+TyrbhANi1U6o82yiukpVuNKmenK8VcUXmQ9UBNZBvDRKf+s5jHz2luf\nGJ9eo2Ipm+AlFJH0GieS5lMyg8qvePQhM6Kt2yIV92tUjuqyQ/ULdVK6/nAQNfhs\n5JcrsNS96B6q+5Kcpobtd4tGxwKBgQDWdd8VmEa/SusCUq+xDLPB1545Wo+6U7b9\n65AJPFvANurEi3tqq5DNV8z/dVsURqLUW/mHBOwNRlZwaFNTUDiiSikpOzEBM/Kj\nci96sWR9b6Vvi2QdWRV/LaikN1Ez7Sogd+Gbvb94rcpfeDJuYD9k3E2ONSP6Fr+2\ntAM9/itf/QKBgQC7blrFsHM8a++zoK3l2Gh/lN63hcBat20A9v/Y3s0OutMwAkVQ\nPuHV5Kh4IE3JXxGvi/0msfZ7N70CrpjtHAdJOWY8vrFEH34cIMuhOsG65G2dGkxy\nReqQkJqfvwn2O9RK8omeZP/Yi63f2wWdyVAkOVNWP8YpBY+lleD4IB9FEQKBgCpB\nhVgq7d2pNE773dRDzXNnOazRVDqBXi//5xx6wRVJfM6HMSUecuSS5Th8dhqzpEe1\na6AX7y7aahOx6qEi0FXhyORv3kL/alKcIBDLvSIeCnnE/E/wCpVJeM4KfDyAAWnu\nVQyrwXtUuug+takb28HSypE3lUQkkVe9dloTp5Y9AoGBAIv67oLzsQLfQQgzCNDC\ny9QeUGTchDwoPtwpgj/ZgByjWa+0CFjd6Lnv/QFWEmhbAW+IlNz1xdetXDPdD9Ew\n2yEk0zNlo114tSdQeASo/LODjJi+PwJGRY9LalXLV2AmCDnUAg1/ixe86eLXasOj\nXb1kcGThUtElvx6+uZu8J2i0\n-----END PRIVATE KEY-----\n",
    client_email: "firebase-adminsdk-fbsvc@nexcuse-50a07.iam.gserviceaccount.com",
    client_id: "103267714061747801125",
    auth_uri: "https://accounts.google.com/o/oauth2/auth",
    token_uri: "https://oauth2.googleapis.com/token",
    auth_provider_x509_cert_url: "https://www.googleapis.com/oauth2/v1/certs",
    client_x509_cert_url: "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40nexcuse-50a07.iam.gserviceaccount.com",
    universe_domain: "googleapis.com"
  }
};

export function env(name, fallback) {
  return process.env[name] || fallback || DEFAULTS[name] || "";
}
