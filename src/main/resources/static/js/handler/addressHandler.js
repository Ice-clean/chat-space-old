/** 通讯录处理器 */
class AddressHandler {
    /** 用户信息、控制中心使用权 */
    #user; #cc

    constructor(user, controlCenter) {
        this.#user = user
        this.#cc = controlCenter

        // 绑定监听事件
        this.#bindClick()
    }

    start() {
        // 初始化群聊和好友列表
        this.#initGroupAndFriendList()
    }

    /** 初始化群聊列表 */
    #initGroupAndFriendList() {
        $.get(HOST + "/space/group/list", {userId: user.userId}, (data) => {
                // 将群聊设置到群聊列表服务中
                let groupList = data.data.groupList
                this.#cc.setData(GROUP_SERVICE, "groupList", [...groupList])
            }, "json"
        );
        $.get(HOST + "/space/friend/list", {userId: user.userId}, (data) => {
                // 将群聊设置到群聊列表服务中
                let friendList = data.data.friendList
                this.#cc.setData(FRIEND_SERVICE, "friendList", [...friendList])
            }, "json"
        );
    }

    /** 为列表绑定点击监听事件 */
    #bindClick() {
        $(".messageList")[0].onclick = () => {
        }
    }
}