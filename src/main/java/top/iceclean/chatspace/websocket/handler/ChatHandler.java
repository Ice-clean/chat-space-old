package top.iceclean.chatspace.websocket.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
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
import top.iceclean.chatspace.po.Site;
import top.iceclean.chatspace.po.User;
import top.iceclean.chatspace.service.UserService;
import top.iceclean.chatspace.websocket.processor.MessageProcessor;
import top.iceclean.chatspace.websocket.processor.SiteProcessor;
import top.iceclean.chatspace.websocket.share.ServerSession;
import top.iceclean.chatspace.websocket.share.SpaceUnit;

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
            case SEND_MESSAGE: MessageProcessor.sendMessage(wsContent); break;
            case SITE_UPDATE: SiteProcessor.updateSite(wsContent, ServerSession.getSession(ctx.channel()).getUser().getUserId()); break;
            default:
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            // 握手成功后，就升级为 WebSocket 了，要移出两个权限校验器
            ctx.channel().pipeline().remove("AuthIn");
            ctx.channel().pipeline().remove("AuthOut");

            // 为新用户进行初始化
            newInSpace(ctx.channel());
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
        MessageProcessor.changeOnline(user.getUserId(), false);

        System.out.println("用户下线：" + user.getUserName());
        super.channelInactive(ctx);
    }

    private void newInSpace(Channel channel) {
        // 获取当前用户
        User user = userService.getCurrentUser();

        // 为用户新建会话并绑定通道
        new ServerSession(user, channel);
        // 为用户初始化位置
        Site site = new Site();
        site.setSpaceId(0);
        site.setX(0);
        site.setY(0);
        site.setVision(2);
        site.setExist(1);
        site.setUserId(user.getUserId());
        System.out.println(site);
        SpaceUnit.newToSpace(site, user);
        SiteProcessor.newSite(site);
        // 向其他用户发送用户上线消息
        MessageProcessor.changeOnline(user.getUserId(), true);

        System.out.println("用户握手成功：" + user.getUserName());
    }
}
