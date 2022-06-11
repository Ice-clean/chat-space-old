/** 于后台交互的 websocket 类 */
class WsMessage {
    /** 消息类型和消息内容 */
    wsType; wsContent

    constructor(wsType, wsContent) {
        this.wsType = wsType;
        this.wsContent = wsContent;
    }

    toJson() {
        return JSON.stringify(this)
    }
}