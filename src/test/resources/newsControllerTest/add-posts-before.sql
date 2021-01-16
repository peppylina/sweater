delete from post;
delete from hibernate_sequence;

insert into post(id, text, tag, user_id, `time`) values
(1, "1st text", "my-tag", 1, 1),
(2, "text", "my-tag", 1, 2),
(3, "text", "alina-tag", 2, 5),
(4, "4st text", "alina-tag", 2, 4);

insert into hibernate_sequence values (10);



