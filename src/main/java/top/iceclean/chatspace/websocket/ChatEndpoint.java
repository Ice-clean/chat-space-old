package top.iceclean.chatspace.websocket;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.iceclean.chatspace.DTO.WsMessageDTO;
import top.iceclean.chatspace.constant.WsType;
import top.iceclean.chatspace.po.Message;
import top.iceclean.chatspace.DTO.MessageDTO;
import top.iceclean.chatspace.po.SessionRequest;
import top.iceclean.chatspace.po.User;
import top.iceclean.chatspace.service.MessageService;
import top.iceclean.chatspace.service.UserService;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : Ice'Clean
 * @date : 2022-05-29
 */
@Component
@ServerEndpoint("/ws/chat/{userId}")
@Slf4j
public class ChatEndpoint implements MessageSender {
    /** 存放所有用户信息 */
    private static final ConcurrentMap<Integer, ChatEndpoint> USER_MAP = new ConcurrentHashMap<>();

    /** 用户服务、朋友服务 */
    private static UserService userService;
    private static MessageService messageService;

    /** 当前用户信息 */
    private User user;
    private Session session;

    /** 注入用户服务 */
    @Autowired
    public void setUserService(UserService userService) {
        ChatEndpoint.userService = userService;
    }

    /** 注入消息服务 */
    @Autowired
    public void setMessageService(MessageService messageService) {
        ChatEndpoint.messageService = messageService;
    }

    @OnOpen
    public void onOpen(@PathParam("userId") Integer userId, Session session) {
        log.info("新用户连接：" + userId);

        // 查询并记录用户具体信息
        this.user = userService.getUserById(userId);
        this.session = session;
        USER_MAP.put(userId, this);

        // 通知用户的好友该用户上线了
        userOnlineHandle(true);
    }

    @OnMessage
    public void onMessage(String msg) {
        // 转化为 ws 消息对象
        WsMessageDTO wsMessageDTO = JSONObject.parseObject(msg, WsMessageDTO.class);
        String wsContent = wsMessageDTO.getWsContent().toString();
        System.out.println("收到消息：" + wsContent);
        // 对不同消息类型作出不同处理
        switch (wsMessageDTO.getWsType()) {
            case SEND_MESSAGE: sendMessageHandle(wsContent); break;
            default:
        }
    }

    @OnClose
    public void onClose() {
        System.out.println("用户断开连接了：" + user.getUserId() + "=" + user.getUserName());
        // 从映射集合中去除用户并在缓存中标记为下线状态
        USER_MAP.remove(user.getUserId());

        // 通知用户的好友该用户下线了
        userOnlineHandle(false);
    }

    @OnError
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    /**
     * 处理聊天消息
     * @param content 聊天消息 JSON 串
     */
    private void sendMessageHandle(String content) {
        // 转化为消息类型
        MessageDTO messageDTO = JSONObject.parseObject(content, MessageDTO.class);
        // 替换换行符
        messageDTO.setContent(messageDTO.getContent().replaceAll("\\n", " "));
        // 保存消息并获取消息实体
        Message message = messageService.saveMessage(messageDTO);
        // 将消息广播给所有目标用户
        castMessage(WsType.SEND_MESSAGE, new DataGenerator.ChatMessage(message));
    }

    /** 用户在线状态改变处理 */
    private void userOnlineHandle(boolean online) {
        // 在缓存中更改用户的在线状态
        userService.setOnline(user.getUserId(), online);
        // 向所有好友发送用户在线状态改变通知
        castMessage(WsType.USER_ONLINE, new DataGenerator.UserOnline(user.getUserId(), online));
    }

    /**
     * 广播消息
     * @param wsType 消息类型
     * @param generator 消息对象生成器
     */
    private void castMessage(WsType wsType, DataGenerator.Generator generator) {
        try {
            // 将消息按照用户情况封装再发送给用户
            ChatEndpoint endpoint;
            for (Integer userId : generator.target()) {
                if ((endpoint = USER_MAP.get(userId)) != null) {
                    endpoint.session.getBasicRemote().sendText(getMessage(wsType, generator.exec(userId)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成一条消息
     * @param wsType 消息类型
     * @param wsContent 消息主体内容
     * @return json 格式的消息实体
     */
    private static String getMessage(WsType wsType, Object wsContent) {
        System.out.println(JSONObject.toJSONString(new WsMessageDTO(wsType, wsContent)));
        return JSONObject.toJSONString(new WsMessageDTO(wsType, wsContent));
    }

    @Override
    public void requestMsg(SessionRequest request) {
        castMessage(WsType.SESSION_REQUEST, new DataGenerator.RequestMessage(request));
    }

    @Override
    public void tokenExpiredMsg(int userId) {
        // 发送 token 失效通知，并主动断开连接
        castMessage(WsType.TOKEN_EXPIRED, new DataGenerator.TokenExpire(userId));
        try {
            USER_MAP.get(userId).session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
