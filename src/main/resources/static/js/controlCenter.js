/** 控制中心，管理各个控件资源和各处理器 */
class ControlCenter {
    /** 各个控件资源 */
    #chatPage
    #chatListService
    #chatName
    #chatService
    #groupService
    #friendService

    /** 各个处理器 */
    #messageListHandler
    #messageHandler
    #websocketHandler
    #addressHandler

    constructor(chatPage) {
        this.#chatPage = chatPage
    }

    /** 设置聊天相关控件 */
    setChatComponent(chatListService, chatName, chatService) {
        this.#chatListService = chatListService
        this.#chatName = chatName
        this.#chatService = chatService
    }

    /** 设置聊天相关处理器 */
    setChatHandler(messageListHandler, messageHandler, websocketHandler) {
        this.#messageListHandler = messageListHandler
        this.#messageHandler = messageHandler
        this.#websocketHandler = websocketHandler
    }

    /** 设置通讯录相关控件 */
    setAddressComponent(groupService, friendService) {
        this.#groupService = groupService
        this.#friendService = friendService
    }

    /** 设置通讯录相关处理器 */
    setAddressHandler(addressHandler) {
        this.#addressHandler = addressHandler
    }

    /** 更改聊天项，在点击消息列表时调用，同步数据 */
    changeChatItem() {
        // 首先获取 chatListService 中的数据
        let data = this.getData(CHAT_LIST_SERVICE)
        // 分别往 chatPage、chatName 和 chatService 注入初始数据
        this.setData(CHAT_NAME, "name", data.name)
        this.setData(CHAT_SERVICE, "sessionId", data.sessionId)
        // 更新聊天消息
        sc(this.#messageHandler, this.#messageHandler.initChatMessage)

        // 将未读消息数置为 0
        for (let i = 0; i < data.messageList.length; i++) {
            let item = data.messageList[i]
            if (item.session.sessionId === data.sessionId) {
                item.session.badgeNum = 0
                break;
            }
        }
        // 更新消息列表
        this.setData(CHAT_LIST_SERVICE, "messageList", [...data.messageList])
    }

    /** 获取指定控件指定键的值 */
    getData(componentConst, dataKey) {
        return dataKey === undefined ?
            this.#getComponent(componentConst)._reactInternals.memoizedProps.data :
            this.#getComponent(componentConst)._reactInternals.memoizedProps.data[dataKey]
    }

    /** 设置指定控件指定键的值 */
    setData(componentConst, dataKey, dataValue) {
        let component = this.#getComponent(componentConst)
        component.setData({[dataKey]: dataValue})
    }

    /** 通过控件常量获取控件 */
    #getComponent(componentConst) {
        let component;
        switch (componentConst) {
            case CHAT_PAGE: component = this.#chatPage; break
            case CHAT_LIST_SERVICE: component = this.#chatListService; break;
            case CHAT_NAME: component = this.#chatName; break;
            case CHAT_SERVICE: component = this.#chatService; break;
            case GROUP_SERVICE: component = this.#groupService; break;
            case FRIEND_SERVICE: component = this.#friendService; break;
        }
        return component
    }
}