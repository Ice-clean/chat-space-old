/** 聊天消息类 */
class Message {
    /** 消息所属会话 ID、消息发送者 ID */
    sessionId; senderId
    /** 消息类型和主体内容 */
    type; content

    constructor(userId, sessionId, type, content) {
        this.sessionId = sessionId;
        this.senderId = userId;
        this.type = type
        this.content = content;
    }
}
