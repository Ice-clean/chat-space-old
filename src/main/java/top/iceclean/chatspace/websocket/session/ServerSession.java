package top.iceclean.chatspace.websocket.session;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import top.iceclean.chatspace.po.User;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 会话类
 * @author : Ice'Clean
 * @date : 2022-09-29
 */
public class ServerSession {
    /** 保存所有的用户连接（ID -> Session） */
    private static final ConcurrentMap<Integer, ServerSession> USER_MAP = new ConcurrentHashMap<>();
    /** 通过通道获取会话的 KEY */
    public static final AttributeKey<ServerSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");

    /** 用户对象 */
    private final User user;
    /** 用户通道 */
    private final Channel channel;

    public ServerSession(User user, Channel channel) {
        System.out.println("绑定用户：" + user);
        this.user = user;
        this.channel = channel;
        // 将会话绑定到用户 ID 和通道上
        USER_MAP.put(user.getUserId(), this);
        channel.attr(SESSION_KEY).set(this);
    }

    public static ServerSession getSession(Channel channel) {
        return channel.attr(SESSION_KEY).get();
    }

    public static ServerSession getSession(int userId) {
        return USER_MAP.get(userId);
    }

    public static void removeSession(int userId) {
        USER_MAP.remove(userId);
    }

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }
}
