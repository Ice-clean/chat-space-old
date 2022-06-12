package top.iceclean.chatspace.websocket;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.iceclean.chatspace.VO.MessageVO;
import top.iceclean.chatspace.VO.UserOnlineVO;
import top.iceclean.chatspace.po.Message;
import top.iceclean.chatspace.po.Session;
import top.iceclean.chatspace.service.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 生成 websocket 所需的数据
 * @author : Ice'Clean
 * @date : 2022-06-03
 */
@Component
public class DataGenerator {
    /** 内部函数式接口，用于生成相应消息 */
    interface Generator {
        /**
         * 获取该消息的目标用户 ID
         * @return 用户 ID 集合
         */
        Set<Integer> target();

        /**
         * 生成发送消息所需的对象数据
         * @param toUserId 目标用户 ID
         * @return 消息对象数据
         */
        Object exec(int toUserId);
    }

    private static UserService userService;
    private static FriendService friendService;
    private static GroupService groupService;
    private static MessageService messageService;
    private static SessionService sessionService;

    /** 注入用户服务 */
    @Autowired
    public void setUserService(UserService userService) {
        DataGenerator.userService = userService;
    }

    /** 注入朋友服务 */
    @Autowired
    public void setFriendService(FriendService friendService) {
        DataGenerator.friendService = friendService;
    }

    /** 注入群聊服务 */
    @Autowired
    public void setGroupService(GroupService groupService) {
        DataGenerator.groupService = groupService;
    }

    /** 注入消息服务 */
    @Autowired
    public void setMessageService(MessageService messageService) {
        DataGenerator.messageService = messageService;
    }

    /** 注入会话服务 */
    @Autowired
    public void setSessionService(SessionService sessionService) {
        DataGenerator.sessionService = sessionService;
    }

    /** 聊天消息生成器 */
    @AllArgsConstructor
    static class ChatMessage implements Generator {
        /** 所需的消息 */
        private final Message message;

        @Override
        public Set<Integer> target() {
            // 会话中的所有用户 ID，就是目标通知用户 ID
            return new HashSet<>(sessionService.getSessionUserId(message.getSessionId()));
        }

        @Override
        public MessageVO exec(int toUserId) {
            return messageService.toMessageVO(message, toUserId, true);
        }
    }

    /** 用户上线状态消息生成器 */
    static class UserOnline implements Generator {
        /** 用户的 ID */
        private final Integer userId;
        /** 是否在线 */
        private final Boolean online;
        /** 用户相关的所有会话的 ID */
        List<Integer> sessionIdList;

        public UserOnline(int userId, boolean online) {
            // 获得必要的元数据
            this.userId = userId;
            this.online = online;

            // 找到所有与用户相关的会话的 ID
            sessionIdList = sessionService.getSessionIdList(userId);
        }

        @Override
        public Set<Integer> target() {
            // 初始化目标集合，首先找到的是该用户所有的好友
            Set<Integer>  targetUserIdSet = new HashSet<>(friendService.getFriendIdList(userId));
            // 然后找到所有和该用户在同一个群聊中的用户 ID
            for (Integer groupKey : groupService.getGroupKeyList(userId)) {
                targetUserIdSet.addAll(groupService.getGroupUserId(groupKey));
            }
            // 最后除去本身，即使所有需要通知的对象
            targetUserIdSet.remove(userId);
            return targetUserIdSet;
        }

        @Override
        public UserOnlineVO exec(int toUserId) {
            return new UserOnlineVO(userId, online, sessionIdList);
        }
    }
}
