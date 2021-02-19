delete from user_role;
delete from usr;

insert into usr(id, active, password, username) values
(1, 1, '$2a$08$gCPUQCmzph4GGyWbeJKC1.is27bhK1Qi99jqIBeK4ALqLHFgSkVKa', 'admin'),
(2, 0, '$2a$08$gCPUQCmzph4GGyWbeJKC1.is27bhK1Qi99jqIBeK4ALqLHFgSkVKa', 'alina'),
(3, 1, '$2a$08$gCPUQCmzph4GGyWbeJKC1.is27bhK1Qi99jqIBeK4ALqLHFgSkVKa', 'dima');

insert into user_role(user_id, roles) values
(1, 'ADMIN'), (1, 'USER'), (2, 'USER'), (3, 'USER');
