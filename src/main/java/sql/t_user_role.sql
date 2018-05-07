create table t_user_role(
    id int(10) not null auto_increment primary key,
    user_id int(10),
    role_id int(10)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;