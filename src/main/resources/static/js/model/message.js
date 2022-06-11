/** 聊天消息类 */
class Message {
    /** 消息类型 */
    type
    /** 消息发送者 ID */
    senderId
    /** 消息接收方 ID */
    receiveId
    /** 消息主体内容 */
    content

    constructor(pageData, content) {
        this.type = pageData.type;
        this.senderId = pageData.user.userId;
        this.receiveId = pageData.receiveId;
        this.content = content;
    }
}
