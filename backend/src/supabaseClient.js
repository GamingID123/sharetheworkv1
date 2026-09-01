import { createClient } from '@supabase/supabase-js';
import { DEFAULTS, env } from './defaults.js';
const url = env('SUPABASE_URL', DEFAULTS.SUPABASE_URL);
const key = env('SUPABASE_SERVICE_ROLE_KEY', DEFAULTS.SUPABASE_SERVICE_ROLE_KEY);
export const supabase = createClient(url, key);
