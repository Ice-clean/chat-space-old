package top.iceclean.chatspace.websocket.processor;

import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.iceclean.chatspace.DTO.MessageDTO;
import top.iceclean.chatspace.DTO.WsMessageDTO;
import top.iceclean.chatspace.constant.WsType;
import top.iceclean.chatspace.po.Message;
import top.iceclean.chatspace.po.SessionRequest;
import top.iceclean.chatspace.service.MessageService;
import top.iceclean.chatspace.service.UserService;
import top.iceclean.chatspace.websocket.common.DataGenerator;
import top.iceclean.chatspace.websocket.common.MessageSender;
import top.iceclean.chatspace.websocket.share.ServerSession;

import java.util.Set;

/**
 * 消息处理器，一个普通的类
 * @author : Ice'Clean
 * @date : 2022-09-29
 */
@Component
public class MessageProcessor implements MessageSender {

    /** 用户服务，消息服务 */
    private static UserService userService;
    private static MessageService messageService;

    @Autowired
    public MessageProcessor(UserService userService, MessageService messageService) {
        MessageProcessor.userService = userService;
        MessageProcessor.messageService = messageService;
    }

    /**
     * 处理聊天消息
     * @param content 聊天消息 JSON 串
     */
    public static void sendMessage(String content) {
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
    public static void changeOnline(int userId, boolean online) {
        // 在缓存中更改用户的在线状态
        userService.setOnline(userId, online);
        // 向所有好友发送用户在线状态改变通知
        castMessage(WsType.USER_ONLINE, new DataGenerator.UserOnline(userId, online));
    }

    /**
     * 向一个用户发送一连串的用户位置更改消息
     * @param userId 消息接收者（一个用户）
     * @param wsType 消息类型（发现 / 消失）
     * @param userIdSet 发生更改的用户（一连串用户）
     */
    public static void changeSiteState(int userId, WsType wsType, Set<Integer> userIdSet) {
        if (userIdSet.size() > 0) {
            castMessage(wsType, new DataGenerator.SiteChange(userId, userIdSet));
        }
    }

    /**
     * 向一连串用户发送一个用户的位置更改消息
     * @param userIdSet 消息接收者（一连串用户）
     * @param wsType 消息类型（发现 / 消失 / 更改）
     * @param userId 发生更改的用户（一个用户）
     */
    public static void changeSiteState(Set<Integer> userIdSet, WsType wsType, int userId) {
        if (userIdSet.size() > 0) {
            castMessage(wsType, new DataGenerator.SiteChange(userIdSet, userId));
        }
    }

    /**
     * 广播消息
     * @param wsType 消息类型
     * @param generator 消息对象生成器
     */
    private static void castMessage(WsType wsType, DataGenerator.Generator generator) {
        System.out.println(generator.target());
        // 将消息按照用户情况封装再发送给用户
        ServerSession session;
        for (Integer userId : generator.target()) {
            if ((session = ServerSession.getSession(userId)) != null) {
                System.out.println("给用户发消息：" + userId);
                session.getChannel().writeAndFlush(getMessageFrame(wsType, generator.exec(userId)));
            }
        }
    }

    /**
     * 生成一条消息
     * @param wsType 消息类型
     * @param wsContent 消息主体内容
     * @return json 格式的消息实体
     */
    private static TextWebSocketFrame getMessageFrame(WsType wsType, Object wsContent) {
        return new TextWebSocketFrame(JSONObject.toJSONString(new WsMessageDTO(wsType, wsContent)));
    }

    @Override
    public void requestMsg(SessionRequest request) {
        // 发送实时的请求消息
        castMessage(WsType.SESSION_REQUEST, new DataGenerator.RequestMessage(request));
    }

    @Override
    public void tokenExpiredMsg(int userId) {
        // 发送实时的 token 失效通知，并主动断开连接
        castMessage(WsType.TOKEN_EXPIRED, new DataGenerator.TokenExpire(userId));
        ServerSession.getSession(userId).getChannel().close();
    }
}
