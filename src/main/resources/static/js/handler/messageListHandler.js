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
        $.get(HOST + "/space/session/list", {userId: user.userId},
            (data) => {
                // 将消息列表设置到消息列表服务中
                let messageList = data.data.messageList
                this.#cc.setData(CHAT_LIST_SERVICE, "messageList", [...messageList])

                // 将消息列表第一列信息设置到消息列表服务中（默认显示第一列消息，至少也会显示自己的聊天框）
                let firstChat = messageList[0]
                this.#cc.setData(CHAT_LIST_SERVICE, "sessionId", firstChat.session.sessionId)
                this.#cc.setData(CHAT_LIST_SERVICE, "name", firstChat.session.name)

                // 然后触发聊天项数据更新
                sc(this.#cc, this.#cc.changeChatItem)
            }, "json"
        );
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
     * @param friend 用户与当前用户所处的私聊接收域 ID
     * @param group 用户与当前用户共有的群聊接收域 ID 列表
     */
    userOnlineHandle(online, friend, group) {
        // 获取消息列表
        let data = this.#cc.getData(CHAT_LIST_SERVICE, "messageList")
        // 更改接收域状态
        for (let i = 0; i < data.length; i++) {
            let item = data[i]
            if (item.type === 0 && item.receiveId === friend) {
                item.online = online ? "1" : "0"
            } else if (item.type === 1 && group.indexOf(item.receiveId) > -1) {
                let onlineArr = item.onlineRecord.split(" / ")
                let nowOnline = parseInt(onlineArr[0]) + (online ? 1 : -1)
                item.onlineRecord = nowOnline + " / " + onlineArr[1]
            }
        }
        // 更新消息列表数据
        this.#cc.setData(CHAT_LIST_SERVICE, "messageList", [...data])
    }

    updateMessageList(message) {
        // 获取消息列表
        let data = this.#cc.getData(CHAT_LIST_SERVICE, "messageList")
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
}