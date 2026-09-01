-- Demo seed data (development only)
insert into users (name, email, password_hash, class_name, section, role, status) values
('Admin User','admin@school.edu','$2a$10$demoHashAdmin','12','A','ADMIN','ACTIVE'),
('Mr. Sharma','sharma@school.edu','$2a$10$demoHashMod','8','A','MODERATOR','ACTIVE'),
('Ms. Verma','verma@school.edu','$2a$10$demoHashMod2','8','A','MODERATOR','ACTIVE'),
('Pratyush','pratyush@school.edu','$2a$10$demoHashStudent','8','A','STUDENT','ACTIVE'),
('Aarav Singh','aarav@school.edu','$2a$10$demoHashStudent2','8','A','STUDENT','ACTIVE');

insert into classes (name) values ('8'),('9'),('10');
insert into sections (class_id, name) select id,'A' from classes where name='8';
insert into sections (class_id, name) select id,'B' from classes where name='8';

insert into homework (subject, title, description, class_name, section, due_date, teacher_name) values
('Mathematics','Linear Equations','Solve Q1-10 Ex 2.3','8','A', now() + interval '1 day','Mr. Sharma'),
('Science','Photosynthesis','Complete worksheet','8','A', now() + interval '0 day','Ms. Verma');

insert into announcements (title, description, author_name, target_class, target_section, is_important) values
('PTM on 5th September','Parent-teacher meeting at 10 AM for 8-A','Admin','8','A', true),
('Science Exhibition','Submit ideas by 3rd Sep','Ms. Verma','8', null, false);

insert into conversations (name, is_group, is_community) values ('8-A', true, false), ('Everyone', false, true);
