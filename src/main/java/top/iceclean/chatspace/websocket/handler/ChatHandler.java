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
import top.iceclean.chatspace.DTO.WsMessageDTO;
import top.iceclean.chatspace.po.User;
import top.iceclean.chatspace.service.UserService;
import top.iceclean.chatspace.websocket.session.ServerSession;

/**
 * 聊天处理器（入栈）
 * @author : Ice'Clean
 * @date : 2022-09-29
 */
@Component
@ChannelHandler.Sharable
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        ChatHandler.userService = userService;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 将文本转化为 ws 消息对象
        WsMessageDTO wsMessageDTO = JSONObject.parseObject(msg.text(), WsMessageDTO.class);
        String wsContent = wsMessageDTO.getWsContent().toString();

        // 对不同消息类型作出不同处理
        switch (wsMessageDTO.getWsType()) {
            case SEND_MESSAGE: MessageHandler.sendMessage(wsContent); break;
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
            MessageHandler.changeOnline(user.getUserId(), true);
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
        MessageHandler.changeOnline(user.getUserId(), false);

        System.out.println("用户下线：" + user.getUserName());
        super.channelInactive(ctx);
    }
}
