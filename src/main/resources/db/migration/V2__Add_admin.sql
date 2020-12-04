insert into usr (id, username, password, active, email)
    values (1, 'admin', '$2y$08$vNwlZL8gVgd2H3V1BSDazutPKzYEBVaIW1J9N5CMdtRirYXVweTIC', 1, 'admin@admin.com');

insert into user_role (user_id, roles)
    values (1, 'USER'), (1, 'ADMIN');