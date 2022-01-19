# 初始化分类
drop table if exists `category`;
create table `category` (
    `id` bigint not null comment 'id',
    `parent` bigint not null default 0 comment '父id',
    `name` varchar(50) not null comment '名称',
    `sort` int comment '顺序',
    primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='分类';

insert into `category` (id, parent, name, sort) values (100, 000, '前端开发', 100);
insert into `category` (id, parent, name, sort) values (101, 100, 'Vue', 101);
insert into `category` (id, parent, name, sort) values (102, 100, 'HTML & CSS', 102);
insert into `category` (id, parent, name, sort) values (200, 000, 'JAVA', 200);
insert into `category` (id, parent, name, sort) values (201, 200, '基础应用', 201);
insert into `category` (id, parent, name, sort) values (202, 200, '框架应用', 202);
insert into `category` (id, parent, name, sort) values (300, 000, 'Python', 300);
insert into `category` (id, parent, name, sort) values (301, 300, '基础应用', 301);
insert into `category` (id, parent, name, sort) values (302, 300, '进阶方向引用', 302);
insert into `category` (id, parent, name, sort) values (400, 000, '数据库', 400);
insert into `category` (id, parent, name, sort) values (401, 400, 'MySql', 401);
insert into `category` (id, parent, name, sort) values (500, 000, '其他', 500);
insert into `category` (id, parent, name, sort) values (501, 500, '服务器', 501);
insert into `category` (id, parent, name, sort) values (502, 500, '开发工具', 502);
insert into `category` (id, parent, name, sort) values (503, 500, '热门服务端语言', 503);

# 初始化ebook
drop table if exists `ebook`;
create table `ebook` (
  `id` bigint not null comment 'id',
  `name` varchar(50) comment '名称',
  `category1_id` bigint comment '分类1',
  `category2_id` bigint comment '分类2',
  `description` varchar(200) comment '描述',
  `cover` varchar(200) comment '封面',
  `doc_count` int comment '文档数',
  `view_count` int comment '阅读数',
  `vote_count` int comment '点赞数',
  primary key (`id`)
) engine = innodb default charset=utf8mb4 comment='电子书';

insert into `ebook` (id, name, category1_id, category2_id, description)  values (1,'Spring boot 入门教程', 200, 202, '零基础出入门 Java 开发,企业级应用开发最佳首选框架');
insert into `ebook` (id, name, category1_id, category2_id, description)  values (2,'Vue 入门教程',100, 101, '零基础入门 Vue 开发,企业级应用开发最佳首选框架');
insert into `ebook` (id, name, description)  values (3,'Python 入门教程','零基础入门 Python 开发,企业级开发最佳首选框架');
insert into `ebook` (id, name, description)  values (4,'Mysql 入门教程','零基础入门 Mysql 开发,企业级开发最佳首选框架');
insert into `ebook` (id, name, description)  values (5,'Orcale 入门教程','零基础入门 Orcale 开发,企业级开发最佳首选框架');

#文档表

drop table if exists `doc`;
create table `doc` (
    `id` bigint not null comment 'id',
    `ebook_id` bigint not null default 0 comment '电子书id',
    `parent` bigint not null default 0 comment '父id',
    `name` varchar(50) not null comment '名称',
    `sort` int comment '顺序',
    `view_count` int default 0 comment '阅读数',
    `vote_count` int default 0 comment '点赞数0',
    primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='文档';

insert into `doc`(id, ebook_id, parent, name, sort, view_count, vote_count) value (1, 1, 0,'文档1',1, 0, 0);
insert into `doc`(id, ebook_id, parent, name, sort, view_count, vote_count) value (2, 1, 1,'文档1.1',1, 0, 0);
insert into `doc`(id, ebook_id, parent, name, sort, view_count, vote_count) value (3, 1, 0,'文档2',2, 0, 0);
insert into `doc`(id, ebook_id, parent, name, sort, view_count, vote_count) value (4, 1, 3,'文档2.1',1, 0, 0);
insert into `doc`(id, ebook_id, parent, name, sort, view_count, vote_count) value (5, 1, 3,'文档2.2',2, 0, 0);
insert into `doc`(id, ebook_id, parent, name, sort, view_count, vote_count) value (6, 1, 5,'文档2.2.1',1, 0, 0);

#文档内容
drop table if exists `content`;
create table `content` (
    `id` bigint not null comment '文档id',
    `content` mediumtext not null comment '内容',
    primary key (`id`)
) engine=innodb default  charset=utf8mb4 comment='文档内容';