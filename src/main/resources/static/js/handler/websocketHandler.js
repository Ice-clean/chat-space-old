/** websocket 处理器 */
class WebsocketHandler {
    /** ws 连接、user 对象、控制中心使用权 */
    #ws; #user; #cc
    /** 消息列表处理器 */
    #messageListHandler
    /** 消息处理器 */
    #messageHandler

    constructor(user, messageListHandler, messageHandler, controlCenter) {
        this.#user = user
        this.#messageListHandler = messageListHandler
        this.#messageHandler = messageHandler
        this.#cc = controlCenter

        // 连接 websocket
        this.#connectWebsocket()
        // 心跳包，每 5 分钟一次
        tryDo((success) => {
            this.sendMessage(HEART_BEAT, "heart beat of " + this.#user.userId)
        }, 5 * 60 * 1000)
    }

    /** 连接 websocket */
    #connectWebsocket() {
        // 构建连接
        this.#ws = connectWebsocket(this.#user.userId);

        // 消息分发
        this.#ws.onmessage = (event) => {
            // 解析收到的消息
            console.log(event.data)
            let message = JSON.parse(event.data);
            console.log(message)
            switch (message.wsType) {
                case USER_ONLINE: this.#userOnlineHandle(message.wsContent); break;
                case SEND_MESSAGE: this.#sendMessageHandle(message.wsContent); break;
            }
        }

        // 断线重连
        this.#ws.onclose = (event) => {
            console.log("断开", event)
            scTime(this, this.#connectWebsocket, 100)
        }

        // 错误排查
        this.#ws.onerror = (event) => {
            console.log("出错", event)
        }
    }

    /** 发送 ws 消息 */
    sendMessage(wsType, wsContent) {
        console.log(new WsMessage(wsType, wsContent).toJson())
        if (this.#ws !== undefined) {
            this.#ws.send(new WsMessage(wsType, wsContent).toJson());
        }
    }

    /** 处理用户在线状态改变 */
    #userOnlineHandle(userOnline) {
        // 处理消息列表的用户状态更变
        console.log(userOnline.sessionIdList)
        this.#messageListHandler.userOnlineHandle(userOnline.online, userOnline.sessionIdList)
        // TODO 处理好友列表的用户状态更变
        // TODO 处理群聊列表的用户状态更变
    }

    /** 处理消息接收 */
    #sendMessageHandle(msg) {
        // 先更新消息列表，并在群聊且非本省时，加上发送者昵称（深拷贝消息）
        let msgTemp = $.extend(true, {}, msg)
        if (!msg.self && msg.session.type === 1) msgTemp.content = msg.sender.nickName + "：" + msg.content
        this.#messageListHandler.updateMessageList(msgTemp)

        // 获取当前聊天数据
        let data = this.#cc.getData(CHAT_SERVICE)
        // 判断是否在当前窗口
        if (data.sessionId === msg.session.sessionId) {
            // 在的话则添加消息，非用户本身的群聊消息需要将前面的昵称提示去掉
            this.#messageHandler.appendMessage(msg);
        }
    }
}