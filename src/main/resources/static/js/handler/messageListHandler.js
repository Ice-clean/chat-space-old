/** 消息列表处理器 */
class MessageListHandler {
    /** 用户信息、控制中心使用权 */
    #user; #cc

    constructor(user, controlCenter) {
        this.#user = user
        this.#cc = controlCenter

        // 绑定监听事件
        this.#bindClick()
        // 初始化消息列表
        this.#initChatList()
    }

    /** 初始化消息列表 */
    #initChatList() {
        getSessionList(this.#user.userId, (data) => {
            // 获取消息列表并格式化时间
            let messageList = data.data.messageList
            MessageListHandler.#shortTimeMessageList(messageList)
            // 将消息列表设置到消息列表服务中
            this.#cc.setData(CHAT_LIST_SERVICE, "messageList", [...messageList])

            // 将消息列表第一列信息设置到消息列表服务中（默认显示第一列消息，至少也会显示自己的聊天框）
            let firstChat = messageList[0]
            this.#cc.setData(CHAT_LIST_SERVICE, "sessionId", firstChat.session.sessionId)
            this.#cc.setData(CHAT_LIST_SERVICE, "name", firstChat.session.name)

            // 然后触发聊天项数据更新
            sc(this.#cc, this.#cc.changeChatItem)
        })
    }

    /** 为列表绑定点击监听事件 */
    #bindClick() {
        // 分别往 chatPage、chatName 和 chatService 注入聊天项数据
        $(".messageList").bind("click", () => {
            sc(this.#cc, this.#cc.changeChatItem)
        })
    }

    /**
     * 处理消息列表的用户状态改变事件
     * @param online 用户状态最新数据
     * @param sessionIdList 与用户相关的会话的 ID
     */
    userOnlineHandle(online, sessionIdList) {
        // 获取消息列表
        let data = this.#cc.getData(CHAT_LIST_SERVICE, "messageList")
        // 更改接收域状态
        for (let i = 0; i < data.length; i++) {
            let item = data[i]
            if (sessionIdList.includes(item.session.sessionId)) {
                if (item.session.type === 0) {
                    item.session.online = online ? 1 : 0
                } else if (item.session.type === 1) {
                    item.session.online += online ? 1 : -1
                }
            }
        }
        // 更新消息列表数据
        this.#cc.setData(CHAT_LIST_SERVICE, "messageList", [...data])
    }

    updateMessageList(message) {
        // 获取消息列表，并简化时间
        let data = this.#cc.getData(CHAT_LIST_SERVICE, "messageList")
        data.sendTime = MessageListHandler.#shortTime(data.sendTime)
        for (let i = 0; i < data.length; i++) {
            let item = data[i]
            // 找到该条消息，删除原消息并将最新消息插入到第一位
            if (item.session.sessionId === message.session.sessionId) {
                data.splice(i, 1)
                data.unshift(message)
            }
        }
        this.#cc.setData(CHAT_LIST_SERVICE, "messageList", [...data])
    }

    /** 精简消息列表时间格式 */
    static #shortTimeMessageList(messageList) {
        for (let i = 0; i < messageList.length; i++) {
            messageList[i].sendTime = this.#shortTime(messageList[i].sendTime)
        }
    }

    /** 精简时间格式 */
    static #shortTime(sendTime) {
        // 重解析时间为数组
        let times = MessageListHandler.#reparseTime(sendTime);
        // 获取年
        let time = new Date()
        let year = time.getFullYear();
        let day = time.getDay();
        // 精简日期
        if (year > times[0]) {
            sendTime = times[0]+ "年" + times[1] + "月"
        } else if (day > times[2]) {
            sendTime = times[1] + "月" + times[2] + "日"
        } else {
            sendTime =
                (times[3] < 10 ? "0" : "") + times[3] + ":" +
                (times[4] < 10 ? "0" : "") + times[4]
        }
        return sendTime
    }

    static #reparseTime(sendTime) {
        let reparse = [0, 0, 0, 0, 0, 0];
        let dateTime = sendTime.split(" ");
        let date = dateTime[0].split("-");
        let sTime = dateTime[1].split(":");
        reparse[0] = parseInt(date[0]);
        reparse[1] = parseInt(date[1]);
        reparse[2] = parseInt(date[2]);
        reparse[3] = parseInt(sTime[0]);
        reparse[4] = parseInt(sTime[1]);
        reparse[5] = parseInt(sTime[2]);

        return reparse
    }
}