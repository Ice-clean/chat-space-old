
-- 用户表
create table t_user (
    user_id     int       auto_increment comment '用户 ID' primary key,
    user_name   varchar(20)              comment '用户名',
    user_pass   varchar(20)              comment '用户密码',
    sex         varchar(10)              comment '用户性别',
    nick_name   varchar(20)              comment '用户昵称',
    avatar      varchar(20)              comment '用户头像',
    freeze_time timestamp null default null         comment '用户冻结的时间',
    delete_time timestamp null default null         comment '用户注销的时间',
    create_time timestamp default CURRENT_TIMESTAMP comment '账号创建时间',
    update_time timestamp default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '修改时间'
);

-- 用户表字段区分大小写
ALTER TABLE t_user MODIFY COLUMN user_name VARCHAR(20) BINARY CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL;

-- 好友表
create table t_friend (
    map_id      int       auto_increment comment '映射 ID' primary key,
    friend_id   int                      comment '好友 ID，唯一标识',
    user_id     int                      comment '用户 ID',
    to_user_id  int                      comment '好友用户 ID',
    last_msg_id int                      comment '最后一次阅读的消息 ID',
    mark_name   varchar(20)              comment '好友备注',
    delete_time timestamp null default null         comment '取消好友的时间',
    create_time timestamp default CURRENT_TIMESTAMP comment '成为好友的时间'
);

-- 好友表索引
create index user_id_index on t_friend (user_id);

-- 群聊表
create table t_group (
    group_id    int       auto_increment comment '群聊 ID' primary key,
    creator_id  int                      comment '群主 ID',
    group_name  varchar(20)              comment '群聊名称',
    avatar      varchar(20)              comment '群聊头像',
    number      int                      comment '群聊人数',
    delete_time timestamp null default null         comment '删除群聊时间',
    create_time timestamp default CURRENT_TIMESTAMP comment '群聊创建时间',
    update_time timestamp default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP  not null comment '修改时间'
);

-- 用户群聊表
create table t_user_group (
    map_id      int       auto_increment comment '映射 ID' primary key,
    user_id     int                      comment '用户 ID',
    group_id    int                      comment '群聊 ID',
    last_msg_id int                      comment '最后一条阅读的消息 ID',
    mark_name   varchar(20)              comment '用户群聊备注',
    delete_time timestamp null default null         comment '用户退群时间',
    create_time timestamp default CURRENT_TIMESTAMP comment '用户进群时间'
);

-- 用户群聊表索引
create index user_id_index on t_user_group (user_id);
create index group_id_index on t_user_group (group_id);

-- 会话表
create table t_session (
    session_id  int       auto_increment comment '会话 ID' primary key,
    type        int                      comment '会话类型（0-私聊，1-群聊）',
    target_id   int                      comment '目标 ID（好友/群聊主键）',
    delete_time timestamp null default null         comment '会话删除时间（群主解散群或私聊一方删除好友）',
    create_time timestamp default CURRENT_TIMESTAMP comment '会话创建时间',
    update_time timestamp default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '会话更新时间'
);

-- 会话表索引
create index target_id_index on t_session (target_id);

-- 会话聊天记录表
create table t_session_message (
    id          int       auto_increment comment '会话聊天记录 ID' primary key,
    session_id  int                      comment '所属会话 ID',
    msg_id      int                      comment '会话内消息 ID',
    sender_id   int                      comment '消息发送者 ID',
    type        int                      comment '消息类型（0-文本，1-图片，2-视频）',
    content     varchar(255)             comment '消息内容',
    create_time timestamp default CURRENT_TIMESTAMP comment '消息发送时间'
);

-- 会话聊天记录表索引
create index session_id_index on t_session_message (session_id);

-- 会话申请表
create table t_session_request (
    req_id      int       auto_increment comment '请求 ID' primary key,
    type        int                      comment '会话类型',
    sender_id   int                      comment '发送者用户 ID',
    target_id   int                      comment '目标 ID（用户/群聊主键）',
    req_src     varchar(255)             comment '请求来源',
    req_remark  varchar(255)             comment '请求备注信息',
    req_status  int default 0            comment '请求状态（0 未读，1 已读，2 接受，3 拒绝）',
    create_time timestamp default CURRENT_TIMESTAMP comment '请求发送时间'
);

-- 日志头表
create table t_log_head(
    head_id     int       auto_increment comment '日志头 ID' primary key,
    log_level   char(10)                 comment '日志级别',
    log_thread  varchar(255)             comment '日志线程',
    log_site    varchar(255)             comment '日志产生位置',
    log_mode    varchar(20)              comment '日志模式',
    log_type    varchar(20)              comment '日志类型',
    log_url     varchar(255)             comment '请求路径',
    log_parent  varchar(255)             comment '所属父类',
    log_method  varchar(255)             comment '所属方法',
    log_params  varchar(255)             comment '传入参数',
    log_return  text                     comment '请求返回',
    log_stack   text                     comment '日志的堆栈调用信息',
    log_read    int       default 0      comment '是否已读',
    create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间'
);

-- 日志信息表
create table t_log_message(
    message_id  int       auto_increment comment '日志信息 ID' primary key,
    head_id     int                      comment '日志头 ID',
    log_level   char(10)                 comment '日志级别',
    log_site    varchar(255)             comment '日志产生位置',
    log_message text                     comment '日志信息',
    create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间'
)