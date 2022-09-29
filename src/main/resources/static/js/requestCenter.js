/** 请求处理中心，负责发送请求 */

/** 连接 websocket */
function connectWebsocket(userId) {
    return new WebSocket(HOST_WS + "/space/ws/chat", [token]);
}

/** 请求聊天消息列表 */
function getSessionList(userId, callback) {
    $.ajax({
        type: "GET",
        dataType: "json",
        headers: {token: token},
        url: HOST + "/space/session/list",
        data: {userId: userId},
        success: (data) => callback(data)
    })
}

/** 请求历史聊天记录 */
function getSessionHistory(userId, sessionId, page, callback) {
    $.ajax({
        type: "GET",
        dataType: "json",
        headers: {token: token},
        url: HOST + "/space/session/history",
        data: {userId: userId, sessionId: sessionId, page: page},
        success: (data) => callback(data)
    })
}

/** 获取未读申请 */
function getRequestBadge(userId, callback) {
    $.ajax({
        type: "GET",
        dataType: "json",
        headers: {token: token},
        url: HOST + "/space/request/badge",
        data: {userId: userId},
        success: (data) => callback(data)
    })
}

/** 获取申请列表 */
function getRequestList(userId, callback) {
    $.ajax({
        type: "GET",
        dataType: "json",
        headers: {token: token},
        url: HOST + "/space/request/list",
        data: {userId: userId},
        success: (data) => callback(data)
    })
}

/** 获取群聊列表 */
function getGroupList(userId, callback) {
    $.ajax({
        type: "GET",
        dataType: "json",
        headers: {token: token},
        url: HOST + "/space/group/list",
        data: {userId: userId},
        success: (data) => callback(data)
    })
}

/** 获取好友列表 */
function getFriendList(userId, callback) {
    $.ajax({
        type: "GET",
        dataType: "json",
        headers: {token: token},
        url: HOST + "/space/friend/list",
        data: {userId: userId},
        success: (data) => callback(data)
    })
}

/** 获取群成员列表 */
function getGroupUserList(groupId, callback) {
    $.ajax({
        type: "GET",
        dataType: "json",
        headers: {token: token},
        url: HOST + "/space/group/list/user",
        data: {groupId: groupId},
        success: (data) => callback(data)
    })
}

/** 模糊查询用户 */
function searchUser(key, callback) {
    $.ajax({
        type: "GET",
        dataType: "json",
        headers: {token: token},
        url: HOST + "/space/user/search",
        data: {key: key},
        success: (data) => callback(data)
    })
}