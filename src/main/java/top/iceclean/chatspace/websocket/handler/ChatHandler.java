package top.iceclean.chatspace.websocket.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.iceclean.chatspace.DTO.MessageDTO;
import top.iceclean.chatspace.DTO.WsMessageDTO;
import top.iceclean.chatspace.constant.WsType;
import top.iceclean.chatspace.po.Message;
import top.iceclean.chatspace.po.SessionRequest;
import top.iceclean.chatspace.po.User;
import top.iceclean.chatspace.service.MessageService;
import top.iceclean.chatspace.service.UserService;
import top.iceclean.chatspace.websocket.common.MessageSender;
import top.iceclean.chatspace.websocket.common.DataGenerator;
import top.iceclean.chatspace.websocket.session.ServerSession;

/**
 * 聊天处理器（入栈）
 * @author : Ice'Clean
 * @date : 2022-09-29
 */
@Component
@ChannelHandler.Sharable
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> implements MessageSender {

    /** 用户服务，消息服务 */
    private static UserService userService;
    private static MessageService messageService;

    public ChatHandler() {
    }

    @Autowired
    public ChatHandler(UserService userService, MessageService messageService) {
        ChatHandler.userService = userService;
        ChatHandler.messageService = messageService;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 将文本转化为 ws 消息对象
        WsMessageDTO wsMessageDTO = JSONObject.parseObject(msg.text(), WsMessageDTO.class);
        String wsContent = wsMessageDTO.getWsContent().toString();

        // 对不同消息类型作出不同处理
        switch (wsMessageDTO.getWsType()) {
            case SEND_MESSAGE: sendMessageHandle(wsContent); break;
            default:
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            // 握手成功后，就升级为 WebSocket 了，要移出两个权限校验器
            ctx.channel().pipeline().remove("AuthIn");
            ctx.channel().pipeline().remove("AuthOut");
            // 然后为当前用户绑定通道
            User user = userService.getCurrentUser();
            new ServerSession(user, ctx.channel());
            userOnlineHandle(user.getUserId(), true);
            System.out.println("用户握手成功：" + user.getUserName());
        } else if (evt instanceof IdleStateEvent) {
            // 连接读写超时
            System.out.println("用户读写超时：" + ServerSession.getSession(ctx.channel()).getUser().getUserName());
            IdleStateEvent stateEvent = (IdleStateEvent) evt;
            if (stateEvent.state() == IdleState.READER_IDLE) {
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 断开连接时，移出会话
        User user = userService.getCurrentUser();
        ServerSession.removeSession(user.getUserId());
        userOnlineHandle(user.getUserId(), false);

        System.out.println("用户下线：" + user.getUserName());
        super.channelInactive(ctx);
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
    private void userOnlineHandle(int userId, boolean online) {
        // 在缓存中更改用户的在线状态
        userService.setOnline(userId, online);
        // 向所有好友发送用户在线状态改变通知
        castMessage(WsType.USER_ONLINE, new DataGenerator.UserOnline(userId, online));
    }

    /**
     * 广播消息
     * @param wsType 消息类型
     * @param generator 消息对象生成器
     */
    private void castMessage(WsType wsType, DataGenerator.Generator generator) {
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
