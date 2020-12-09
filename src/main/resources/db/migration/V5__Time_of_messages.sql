drop TABLE IF EXISTS `spring_session_attributes`;

drop TABLE IF EXISTS `sweater`.`spring_session`;

alter table message
        add column `time` bigint default 1;