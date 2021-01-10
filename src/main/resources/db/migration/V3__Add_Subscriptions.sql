drop TABLE IF EXISTS spring_session_attributes;

drop TABLE IF EXISTS spring_session;


create table user_subscriptions (
       profile_id bigint not null references usr (id),
       subscriber_id bigint not null references usr (id),
       primary key (profile_id, subscriber_id)
);

