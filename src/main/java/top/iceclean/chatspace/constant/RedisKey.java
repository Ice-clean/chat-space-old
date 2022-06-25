package top.iceclean.chatspace.constant;

/**
 * 缓存中的键名
 * @author : Ice'Clean
 * @date : 2022-06-08
 */
public class RedisKey {
    /** 通用头 */
    public static final String HEAD = "chatspace:";

    /** 用户注册验证码缓存 */
    public static final String USER_CODE_HASH = HEAD + "code";
    /** 用户登录缓存 */
    public static final String USER_LOGIN = HEAD + "login:";

    /** 在线用户统计，位图偏移量为用户 ID，值表是否在线 */
    public static final String USER_ONLINE_BIT = HEAD + "user:online";
    /** 群聊在线人数统计，域为群聊 ID，值为在线人数 */
    public static final String GROUP_ONLINE_HASH = HEAD + "group:online";
}
