/** 用户上线信息类 */
class UserOnline {
    /** 用户 ID */
    userId
    /** 用户上线状态 */
    online
    /** 发生状态更变的私聊接收域 */
    friend
    /** 发生状态更变的群聊接收域 */
    group

    constructor(content) {
        this.userId = content.userId
        this.online = content.online
        this.friend = content.friend
        this.group = content.group
    }
}