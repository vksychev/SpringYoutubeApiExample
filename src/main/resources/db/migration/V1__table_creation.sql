create
sequence
hibernate_sequence start 1 increment 1;

create table categories
(
    id int4 not null,
    name varchar(255) not null ,
    primary key (id)
);
create table regions
(
    id int4 not null,
    abb  varchar(255) not null UNIQUE ,
    name varchar(255) not null UNIQUE ,
    primary key (id)
);
create table video_region
(
    video_id varchar(255) not null,
    region_id int4 not null,
    primary key (video_id, region_id)
);
create table videos
(
    id            varchar(255) not null,
    comments int4,
    creation_date timestamp,
    dislikes int4,
    likes int4,
    name          varchar(2048) not null ,
    trend_moment  timestamp not null ,
    trend_num int4,
    views int4,
    category_id int4,
    user_id       varchar(255),
    primary key (id)
);
create table youtubers
(
    id   varchar(255) not null,
    name varchar(2048) not null ,
    subs int4,
    primary key (id)
);
alter table if exists video_region add constraint video_region_region_fk foreign key (region_id) references regions;
alter table if exists video_region add constraint video_region_video_fk foreign key (video_id) references videos;
alter table if exists videos add constraint video_category_fk foreign key (category_id) references categories;
alter table if exists videos add constraint video_youtuber_fk foreign key (user_id) references youtubers;