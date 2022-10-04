package top.iceclean.chatspace.websocket.share;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import top.iceclean.chatspace.VO.SiteVO;
import top.iceclean.chatspace.VO.UserVO;
import top.iceclean.chatspace.po.Site;
import top.iceclean.chatspace.po.User;

import java.util.Set;
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
    private static final AttributeKey<ServerSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");

    /** 用户对象 */
    private final UserVO user;
    /** 用户通道 */
    private final Channel channel;

    /** 用户位置信息 */
    private SiteVO site;
    /** 用户能见集合 */
    private Set<SiteVO> otherSiteSet;

    public ServerSession(User user, Channel channel) {
        System.out.println("绑定用户：" + user);

        this.user = new UserVO(user, true);
        this.channel = channel;

        // 将会话绑定到用户 ID 和通道上
        USER_MAP.put(user.getUserId(), this);
        channel.attr(SESSION_KEY).set(this);
    }

    public void initSite(Site site, Set<SiteVO> otherSiteSet) {
        this.site = new SiteVO(site, user);
        this.otherSiteSet = otherSiteSet;
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

    public UserVO getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }


}
