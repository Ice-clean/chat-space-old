/** 控件注册 */
const CHAT_PAGE = 1
const CHAT_LIST_SERVICE = 2
const CHAT_NAME = 3
const CHAT_SERVICE = 4
const GROUP_SERVICE = 5
const FRIEND_SERVICE = 6

/** 域名 */
const HOST = "http://localhost:18012"
const HOST_WS = "ws://localhost:18012"

/** 同步执行 */
const sc = (th, callback, ...param) => {
    setTimeout(() => callback.call(th, param), 0)
}

const scTime = (th, callback, time, ...param) => {
    setTimeout(() => callback.call(th, param), time)
}

/** 滚动条到最后 */
const scrollBottom = (component) => {
    return () => setTimeout(() => component.scrollTop = component.scrollHeight, 0);
}

/** 轮询执行 */
function tryDo(callback, time) {
    let success = new Success(false)
    let doing = () => {
        if (success.success) return;
        callback(success)
        setTimeout(doing, time);
    }
    return doing
}

/** 轮询成功量 */
class Success {
    success
    constructor(success) {
        this.success = success
    }
}