/** 通讯录处理器 */
class AddressHandler {
    /** 用户信息、控制中心使用权 */
    #user; #cc

    constructor(user, controlCenter) {
        this.#user = user
        this.#cc = controlCenter
    }

    start() {
        console.log("开始查询")
        // 初始化未读申请
        this.#initRequestBadge()
        // 初始化群聊和好友列表
        this.#initGroupAndFriendList()

        // 绑定点击事件
        this.#bindClick()
    }

    /** 初始化未读申请 */
    #initRequestBadge() {
        /** 查询所有未读申请 */
        getRequestBadge(this.#user.userId, (data) => {
            let funcList = this.#cc.getData(FUNC_SERVICE, "funcList")
            funcList[0].badge = data.data.friend
            funcList[1].badge = data.data.group
            this.#cc.setData(FUNC_SERVICE, "funcList", ...funcList)
        })
        /** 查询申请列表 */
        getRequestList(this.#user.userId, (data) => {
            // 将查询到的四种申请放入显示服务中
            console.log("成功", data)
            this.#cc.setData(DISPLAY_SERVICE, "friendRequest", data.data.friendRequest)
            this.#cc.setData(DISPLAY_SERVICE, "requestFriend", data.data.requestFriend)
            this.#cc.setData(DISPLAY_SERVICE, "groupRequest", data.data.groupRequest)
            this.#cc.setData(DISPLAY_SERVICE, "requestGroup", data.data.requestGroup)
        })
    }

    /** 初始化群聊和好友列表 */
    #initGroupAndFriendList() {
        getGroupList(this.#user.userId, (data) => {
            // 将群聊设置到群聊列表服务中
            let groupList = data.data.groupList
            this.#cc.setData(GROUP_SERVICE, "groupList", [...groupList])
        })
        getFriendList(this.#user.userId, (data) => {
            // 将群聊设置到群聊列表服务中
            let friendList = data.data.friendList
            this.#cc.setData(FRIEND_SERVICE, "friendList", [...friendList])
        })
    }

    /** 为功能列表绑定点击监听事件 */
    #bindClick() {
        // 为功能列表绑定点击监听事件
        $(".funcList").bind("click", () => {
            sc(this.#cc, this.#cc.changeFuncItem)
        })
        // 为群聊列表绑定监听事件
        $(".groupList").bind("click", () => {
            sc(this.#cc, this.#cc.changeGroupItem)
        })
        // 为朋友列表绑定监听事件
        $(".friendList").bind("click", () => {
            sc(this.#cc, this.#cc.changeFriendItem)
        })
    }
}