package top.iceclean.chatspace.constant;
/**
 * websocket 通讯类型
 * @author : Ice'Clean
 * @date : 2022-05-29
 */
public enum WsType {
    /** 用户上下线信息 */
    HEART_BEAT,
    /** 用户上下线信息 */
    USER_ONLINE,
    /** 发送消息 */
    SEND_MESSAGE,
    /** 会话请求 */
    SESSION_REQUEST,
    /** token 失效 */
    TOKEN_EXPIRED,
    /** 位置更新请求 */
    SITE_UPDATE,
    /** 位置用户增加响应 */
    SITE_ADD,
    /** 位置用户更改响应 */
    SITE_CHANGE,
    /** 位置用户消失响应 */
    SITE_REMOVE;
}
