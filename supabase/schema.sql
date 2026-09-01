-- ShareTheWork Supabase Schema (PostgreSQL)
-- Run in Supabase SQL Editor

-- Enable UUID
create extension if not exists "uuid-ossp";

-- Users
create table if not exists users (
  id uuid primary key default uuid_generate_v4(),
  name text not null,
  email text unique not null,
  password_hash text not null,
  class_name text not null,
  section text not null,
  profile_picture_url text,
  role text not null check (role in ('STUDENT','MODERATOR','ADMIN')) default 'STUDENT',
  status text not null check (status in ('ACTIVE','SUSPENDED','PENDING')) default 'ACTIVE',
  created_at timestamp with time zone default now()
);
create index idx_users_email on users(email);
create index idx_users_class_section on users(class_name, section);

-- Classes / Sections (simple lookup)
create table if not exists classes ( id uuid primary key default uuid_generate_v4(), name text unique not null );
create table if not exists sections ( id uuid primary key default uuid_generate_v4(), class_id uuid references classes(id), name text not null );

-- Homework
create table if not exists homework (
  id uuid primary key default uuid_generate_v4(),
  subject text not null,
  title text not null,
  description text,
  class_name text not null,
  section text not null,
  due_date timestamp with time zone not null,
  teacher_id uuid references users(id),
  teacher_name text,
  created_at timestamp with time zone default now()
);
create index idx_homework_class_section on homework(class_name, section);
create index idx_homework_due on homework(due_date);

create table if not exists homework_completions (
  homework_id uuid references homework(id) on delete cascade,
  user_id uuid references users(id) on delete cascade,
  status text check (status in ('PENDING','COMPLETED','OVERDUE')) default 'COMPLETED',
  completed_at timestamp with time zone default now(),
  primary key (homework_id, user_id)
);

-- Classwork
create table if not exists classwork (
  id uuid primary key default uuid_generate_v4(),
  subject text not null,
  title text not null,
  description text,
  class_name text not null,
  section text not null,
  teacher_id uuid references users(id),
  teacher_name text,
  created_at timestamp with time zone default now()
);
create index idx_classwork_class_section on classwork(class_name, section);

-- Announcements
create table if not exists announcements (
  id uuid primary key default uuid_generate_v4(),
  title text not null,
  description text not null,
  author_id uuid references users(id),
  author_name text,
  target_class text,
  target_section text,
  is_important boolean default false,
  attachment_url text,
  created_at timestamp with time zone default now()
);

-- Conversations & Messages
create table if not exists conversations (
  id uuid primary key default uuid_generate_v4(),
  name text not null,
  is_group boolean default false,
  is_community boolean default false,
  class_name text,
  section text,
  created_at timestamp with time zone default now()
);

create table if not exists conversation_members (
  conversation_id uuid references conversations(id) on delete cascade,
  user_id uuid references users(id) on delete cascade,
  primary key (conversation_id, user_id)
);

create table if not exists messages (
  id uuid primary key default uuid_generate_v4(),
  conversation_id uuid references conversations(id) on delete cascade,
  sender_id uuid references users(id),
  sender_name text,
  text text not null,
  reply_to_id uuid references messages(id),
  is_deleted boolean default false,
  is_hidden boolean default false,
  moderation_reason text,
  is_read boolean default false,
  created_at timestamp with time zone default now()
);
create index idx_messages_conversation on messages(conversation_id, created_at);

-- Reports / Moderation
create table if not exists reports (
  id uuid primary key default uuid_generate_v4(),
  message_id uuid references messages(id),
  reported_by uuid references users(id),
  reason text not null,
  status text default 'pending',
  created_at timestamp with time zone default now()
);

-- Notifications
create table if not exists notifications (
  id uuid primary key default uuid_generate_v4(),
  user_id uuid references users(id) on delete cascade,
  title text not null,
  body text not null,
  type text not null check (type in ('homework','classwork','announcement','message','admin')),
  is_read boolean default false,
  created_at timestamp with time zone default now()
);
create index idx_notifications_user on notifications(user_id, created_at);

-- AI
create table if not exists ai_conversations (
  id uuid primary key default uuid_generate_v4(),
  user_id uuid references users(id) on delete cascade,
  title text,
  created_at timestamp with time zone default now()
);
create table if not exists ai_messages (
  id uuid primary key default uuid_generate_v4(),
  conversation_id uuid references ai_conversations(id) on delete cascade,
  role text check (role in ('user','assistant')) not null,
  content text not null,
  created_at timestamp with time zone default now()
);

-- Storage bucket should be created via dashboard: sharethework-files
-- RLS Policies (enable after testing): example
-- alter table users enable row level security;
-- create policy "users can read own" on users for select using (auth.uid() = id);

-- Seed helpers inserted via seed.sql
