delete from user_role;
delete from usr;

insert into usr(id, active, password, username) values
(1, 1, '$2a$08$gCPUQCmzph4GGyWbeJKC1.is27bhK1Qi99jqIBeK4ALqLHFgSkVKa', 'admin'),
(2, 1, '$2a$08$gCPUQCmzph4GGyWbeJKC1.is27bhK1Qi99jqIBeK4ALqLHFgSkVKa', 'alina'),
(3, 1, '$2a$08$gCPUQCmzph4GGyWbeJKC1.is27bhK1Qi99jqIBeK4ALqLHFgSkVKa', 'dima'),
(4, 1, '$2a$08$gCPUQCmzph4GGyWbeJKC1.is27bhK1Qi99jqIBeK4ALqLHFgSkVKa', 'oleg'),
(5, 1, '$2a$08$gCPUQCmzph4GGyWbeJKC1.is27bhK1Qi99jqIBeK4ALqLHFgSkVKa', 'valera');

insert into user_role(user_id, roles) values
(1, 'ADMIN'), (1, 'USER'), (2, 'USER'), (3, 'USER'), (4, "USER"), (5, "USER");
