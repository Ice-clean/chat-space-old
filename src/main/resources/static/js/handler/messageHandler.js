// 消息处理器
class MessageHandler {
    /** 用户信息、 websocket 处理器、控制中心使用权 */
    #user; #wsHandler; #cc
    /** 消息输入对象和消息输出对象 */
    #messageInput; #messageOutput;
    /** 图片上传按钮和消息发送按钮 */
    #imageUpload; #messageSend
    /** 当前页数以及当前消息条数 */
    #page = 1; #num = 0

    constructor(user, messageInput, messageOutput, controlCenter) {
        // 初始化成员变量
        this.#user = user
        this.#messageInput = messageInput
        this.#messageOutput = messageOutput
        this.#cc = controlCenter
    }

    start() {
        // 为消息输入对象绑定按键监听
        this.#bindKeypress()
        // 为消息输入对象绑定监听滚动条事件
        this.#bindScroll()
    }

    /** 设置一个 websocket 处理器。用于发送消息 */
    setWsHandler(wsHandler) {
        this.#wsHandler = wsHandler
    }

    /**
     * 绑定按钮
     * @param imageUpload 图片上传
     * @param messageSend 消息发送
     */
    bindButton(imageUpload, messageSend) {
        // 绑定按钮
        this.#imageUpload = imageUpload
        this.#messageSend = messageSend

        // 为消息输入对象绑定点击事件
        this.#bindOnclick()
    }

    /** 初始化聊天消息 */
    initChatMessage() {
        let sessionId = this.#cc.getData(CHAT_SERVICE, "sessionId")
        getSessionHistory(this.#user.userId, sessionId, 1, (data) => {
            // 将历史聊天记录设置到聊天服务中
            this.#cc.setData(CHAT_SERVICE, "historyList", [...data.data.historyList])
            // 重置页数
            this.#page = 1
            // 滚动条到最下方
            this.scrollBottom()
        })
    }

    /** 滚动条滑到底部 */
    scrollBottom() {
        sc(this, scrollBottom(this.#messageOutput))
    }

    /** 为消息输入对象绑定监听按键事件 */
    #bindKeypress() {
        this.#messageInput.keydown((event) => {
            let keyCode = event.keyCode ? event.keyCode : (event.which ? event.which : event.charCode);
            let ctrlKey = event.ctrlKey || event.metaKey;
            if (keyCode === 13 && ctrlKey) {
                let textareaData = this.#cc.getData(CHAT_PAGE, "messageInput") + "\n"
                this.#cc.setData(CHAT_PAGE, "messageInput", textareaData)
            } else if (keyCode === 13) {
                // 获取消息发送者、消息所属会话
                let userId = this.#cc.getData(CHAT_PAGE, "user").userId
                let sessionId = this.#cc.getData(CHAT_SERVICE, "sessionId")
                // 文本框发送的消息为文本消息 type 为 0，同时从文本框中获取内容并清空
                this.#wsHandler.sendMessage(SEND_MESSAGE,
                    new Message(userId, sessionId, 0, this.#readMessage()));
                // 禁止回车的默认换行
                event.preventDefault();
            }
        })

    }

    /** 监听按钮（上传图片、发送按钮） */
    #bindOnclick() {
        this.#imageUpload.bind("click", () => {

        })
        this.#messageSend.bind("click", () => {
        })
    }

    /** 为消息输入对象绑定监听滚动条事件 */
    #bindScroll() {
        this.#messageOutput.onscroll = () => {
            //获取距离页面顶部的距离
            if (this.#messageOutput.scrollTop === 0) {
                // 到达顶部后，再请求一次数据
                let sessionId = this.#cc.getData(CHAT_SERVICE, "sessionId")
                getSessionHistory(this.#user.userId, sessionId, ++this.#page, (data) => {
                    let list = data.data.historyList
                    if (list.length > 0){
                        this.#num += list.length
                        // 将历史聊天记录数组拼接到前边
                        let top = this.#messageOutput.scrollTop
                        let height = this.#messageOutput.scrollHeight
                        this.#prependMessageList(list);
                        sc(this, () => this.#messageOutput.scrollTop = top + this.#messageOutput.scrollHeight - height)
                    }
                })
            }
        }
    }

    /** 向聊天记录中添加数据 */
    appendMessage(message) {
        // 获取原先的消息记录
        let historyList = this.#cc.getData(CHAT_SERVICE, "historyList");
        // 往后面添加本条消息,并将新的消息列表更新回去
        this.#cc.setData(CHAT_SERVICE, "historyList", [...historyList, message])
        // 滚动条滚到最后
        this.scrollBottom()
    }

    /** 在聊天记录前插入列表 */
    #prependMessageList(messageList) {
        // 获取原先的消息记录
        let historyList = this.#cc.getData(CHAT_SERVICE, "historyList");
        // 往前边插入消息列表
        this.#cc.setData(CHAT_SERVICE, "historyList", [...messageList, ...historyList])
    }

    /** 读取消息源（并清空） */
    #readMessage() {
        // 获取消息，然后清空
        let message = this.#cc.getData(CHAT_PAGE, "messageInput")
        this.#cc.setData(CHAT_PAGE, "messageInput", "")
        return message;
    }
}