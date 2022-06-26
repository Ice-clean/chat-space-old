package top.iceclean.chatspace.websocket;

import top.iceclean.chatspace.po.SessionRequest;

/**
 * 聊天 websocket 对外开放的接口
 * @author : Ice'Clean
 * @date : 2022-06-25
 */
public interface MessageSender {

    /**
     * 发送会话请求消息
     * @param sessionRequest 会话请求实体
     */
    void requestMsg(SessionRequest sessionRequest);

    /**
     * token 过期事件
     * @param userId 用户 ID
     */
    void tokenExpiredMsg(int userId);
}
