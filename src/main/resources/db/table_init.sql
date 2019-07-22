create table if not exists ad_item (
`id` bigint primary key auto_increment,
`name` varchar(50),
`title` varchar(50),
`price` int,
`describe` varchar(255),
`sales` int,
`image_url` varchar(50),
`create_time` datetime,
`update_time` datetime,
`remark` varchar(255)
)engine=innodb;

create table if not exists ad_item_stock (
`id` bigint primary key auto_increment,
`stock` int,
`item_id` bigint,
`create_time` datetime,
`update_time` datetime,
`remark` varchar(255)
)engine=innodb;

create table if not exists ad_order (
`id` varchar(255) primary key ,
`user_id` bigint,
`item_id` bigint,
`item_price` int,
`order_price` int,
`amount` int,
`promo_id` bigint,
`create_time` datetime,
`update_time` datetime,
`remark` varchar(255)
)engine=innodb;

create table if not exists ad_promo (
`id` bigint primary key auto_increment,
`name` varchar(255),
start_time datetime,
end_time datetime,
item_id bigint,
promo_item_price int,
create_time datetime,
update_time datetime,
remark varchar(255)
)engine=innodb;