package top.iceclean.chatspace.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.iceclean.chatspace.VO.FriendVO;
import top.iceclean.chatspace.VO.GroupVO;
import top.iceclean.chatspace.VO.SessionVO;
import top.iceclean.chatspace.constant.SessionType;
import top.iceclean.chatspace.mapper.SessionMapper;
import top.iceclean.chatspace.po.*;
import top.iceclean.chatspace.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : Ice'Clean
 * @date : 2022-06-11
 */
@Service
public class SessionServiceImpl implements SessionService {
    @Autowired
    private SessionMapper sessionMapper;
    @Autowired
    private FriendService friendService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private MessageService messageService;


    @Override
    public Session createSession(SessionType type, int targetId) {
        // 新建一个会话并创建
        Session session = new Session(type.value(), targetId);
        sessionMapper.insert(session);
        // 拿到最新的会话并返回
        return sessionMapper.selectById(session.getSessionId());
    }

    @Override
    public List<Integer> getSessionIdList(int userId) {
        // 分别将好友和群聊 ID 转化为会话 ID
        return sessionMapper.selectList(new LambdaQueryWrapper<Session>()
                .select(Session::getSessionId)
                .isNull(Session::getDeleteTime)
                .and(query -> query
                        .eq(Session::getType, SessionType.FRIEND.value())
                        .in(Session::getTargetId, friendService.getFriendKeyList(userId))
                        .or()
                        .eq(Session::getType, SessionType.GROUP.value())
                        .in(Session::getTargetId, groupService.getGroupKeyList(userId))))
                .stream().map(Session::getSessionId).collect(Collectors.toList());
    }

    @Override
    public SessionVO getSessionVO(int sessionId) {
        // 首先获取 session 对象
        Session session = sessionMapper.selectById(sessionId);
        return new SessionVO(sessionId, session.getType());
    }

    @Override
    public SessionVO getSessionVO(int sessionId, int userId) {
        // 首先获取 session 对象
        Session session = sessionMapper.selectById(sessionId);
        SessionVO sessionVO = new SessionVO(sessionId, session.getType());
        // 获取会话中的最新消息 ID
        int latestMsgId = messageService.getLastMsgId(sessionId);
        // 根据类型获取详细信息
        if (session.getType() == SessionType.FRIEND.value()) {
            // 获取好友响应对象
            FriendVO friendVO = friendService.getFriendVO(session.getTargetId(), userId);
            // 获取未读消息数（会话中最新一条消息 ID - 用户上一次最后一条阅读的消息 ID）
            int badgeNum = latestMsgId - friendVO.getLastMsgId();
            // 填充详细信息
            sessionVO.setDetail(friendVO, badgeNum);
        } else {
            // 获取群聊响应对象
            GroupVO groupVO = groupService.getGroupVO(session.getTargetId(), userId);
            // 获取未读消息数
            int badgeNum = latestMsgId - groupVO.getLastMsgId();
            // 填充详细信息
            sessionVO.setDetail(groupVO, badgeNum);
        }
        return sessionVO;
    }

    @Override
    public List<Integer> getSessionUserId(int sessionId) {
        // 最终用户 ID 集合
        List<Integer> userIdList = new ArrayList<>();

        // 获取会话对象并拿到不同类型会话的用户 ID 集合
        Session session = sessionMapper.selectById(sessionId);
        if (session.getType() == SessionType.FRIEND.value()) {
            userIdList = friendService.getFriendUserId(session.getTargetId());
        } else if (session.getType() == SessionType.GROUP.value()) {
            userIdList = groupService.getGroupUserId(session.getTargetId());
        }

        // 最后返回，类型错误即为空集合
        return userIdList;
    }

    @Override
    public void updateLastMsgId(int sessionId, int userId) {
        // 获取会话，根据类型获取具体映射和最新的消息 ID
        Session session = sessionMapper.selectById(sessionId);
        int lastMsgId = messageService.getLastMsgId(sessionId);
        if (session.getType() == SessionType.FRIEND.value()) {
            friendService.updateLastMsgId(session.getTargetId(), userId, lastMsgId);
        } else if (session.getType() == SessionType.GROUP.value()) {
            groupService.updateLastMsgId(session.getTargetId(), userId, lastMsgId);
        }
    }
}
