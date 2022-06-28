package top.iceclean.chatspace.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.iceclean.chatspace.VO.*;
import top.iceclean.chatspace.constant.ResponseStatusEnum;
import top.iceclean.chatspace.constant.SessionType;
import top.iceclean.chatspace.mapper.SessionRequestMapper;
import top.iceclean.chatspace.po.Group;
import top.iceclean.chatspace.pojo.Response;
import top.iceclean.chatspace.po.SessionRequest;
import top.iceclean.chatspace.service.FriendService;
import top.iceclean.chatspace.service.GroupService;
import top.iceclean.chatspace.service.SessionRequestService;
import top.iceclean.chatspace.service.UserService;
import top.iceclean.chatspace.websocket.MessageSender;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : Ice'Clean
 * @date : 2022-06-25
 */
@Service
public class SessionRequestServiceImpl implements SessionRequestService {
    @Autowired
    private SessionRequestMapper requestMapper;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private FriendService friendService;

    @Override
    public Response sendRequest(SessionType type, int senderId, int targetId, String reqSrc, String reqRemark) {
        // 构造请求消息
        SessionRequest request = new SessionRequest(type.value(), senderId, targetId, reqSrc, reqRemark);
        // 直接添加请求记录
        requestMapper.insert(request);
        // 将添加消息实时推送
        messageSender.requestMsg(request);
//        // 获取接收者响应对象
//        UserVO user = userService.toUserVO(userService.getUserById(targetId));
//        // 群聊的话还需要获取群聊对象
//        GroupVO group = null;
//        if (type == SessionType.GROUP) {
//            group = new GroupVO(
//                    groupService.getGroupById(request.getTargetId()),
//                    groupService.getOnlineNum(request.getTargetId()));
//        }
        return new Response(ResponseStatusEnum.OK);
    }

    @Override
    public Response getRequestList(int userId) {
        // 查询出用户所有管理的群聊的 ID
        List<Integer> manageGroupIds = groupService.getUserManageGroups(userId)
                .stream().map(Group::getGroupId).collect(Collectors.toList());
        // 查询用户发送的以及接收的所有申请
        List<SessionRequestVO> requestList = requestMapper.selectList(new LambdaQueryWrapper<SessionRequest>()
                // 用户发送的好友请求和群聊请求
                .eq(SessionRequest::getSenderId, userId)
                // 或者用户接收的好友请求
                .or(orQuery -> orQuery
                        .eq(SessionRequest::getType, 0)
                        .eq(SessionRequest::getTargetId, userId))
                // 或者用户接收的群聊请求（用户为群主或管理员）
                .or(manageGroupIds.size() > 0, orQuery -> orQuery
                        .eq(SessionRequest::getType, 1)
                        .in(SessionRequest::getTargetId, manageGroupIds)))
                .stream().map(request -> toRequestVO(request, userId)).collect(Collectors.toList());
        // 按会话类型和是否为用户发起，分成四类
        Map<Integer, Map<Integer, List<SessionRequestVO>>> collect = requestList.stream()
                .collect(Collectors.groupingBy(SessionRequestVO::getType, Collectors.groupingBy(SessionRequestVO::getKind)));
        // 转化为响应对象返回
        return new Response(ResponseStatusEnum.OK).setData(new SessionRequestListVO(collect));
    }

    @Override
    public Response getRequestBadge(int userId) {
        // 查询出用户所有管理的群聊的 ID
        List<Integer> manageGroupIds = groupService.getUserManageGroups(userId)
                .stream().map(Group::getGroupId).collect(Collectors.toList());
        // 查询域用户相关的所有未读的申请
        Map<Integer, Long> collect = requestMapper.selectList(new LambdaQueryWrapper<SessionRequest>()
                // 未读的记录
                .eq(SessionRequest::getReqStatus, 0)
                .and(query -> query
                        // 好友申请
                        .eq(SessionRequest::getType, 0)
                        .eq(SessionRequest::getTargetId, userId)
                        // 或者群聊申请
                        .or(manageGroupIds.size() > 0, orQuery -> orQuery
                                .eq(SessionRequest::getType, 1)
                                .in(SessionRequest::getTargetId, manageGroupIds))))
                .stream().collect(Collectors.groupingBy(SessionRequest::getType, Collectors.counting()));
        return new Response(ResponseStatusEnum.OK).setData(new RequestBadgeVO(collect));
    }

    @Override
    public Response handleRequest(int reqId, boolean access) {
        // 将目标请求记录的状态置为接受/拒绝
        SessionRequest request = requestMapper.selectById(reqId);
        request.setReqStatus(access ? 2 : 3);
        requestMapper.updateById(request);

        // 同意申请后，需要建立相应的映射关系
        boolean success = true;
        if (access) {
            if (request.getType() == SessionType.FRIEND.value()) {
                success = friendService.becomeFriends(request.getSenderId(), request.getTargetId());
            } else {
                success = groupService.joinGroup(request.getSenderId(), request.getTargetId());
            }
        }

        if (success) {
            return new Response(ResponseStatusEnum.OK);
        }
        return new Response(ResponseStatusEnum.DATABASE_ERROR);
    }

    @Override
    public SessionRequestVO toRequestVO(SessionRequest request, int userId) {
        // 准备返回的响应对象
        SessionRequestVO requestVO = new SessionRequestVO(request, userId);

        // 存储会话类型（好友/群聊）
        String type = "" + request.getType();
        // 存储请求类型（该请求是否为用户发送）
        String kind = "" + (request.getSenderId() == userId ? 1 : 0);

        // 组合成不同种类的响应对象（type 作为十位数，kind 作为个位数的组合）
        switch (type + kind) {
            case "00":
                requestVO.setUser(userService.toUserVO(userService.getUserById(request.getSenderId())));
                break;
            case "10":
                requestVO.setUser(userService.toUserVO(userService.getUserById(request.getSenderId())));
                Group group = groupService.getGroupById(request.getTargetId());
                requestVO.setGroup(new GroupVO(group, groupService.getOnlineNum(group.getGroupId())));
                break;
            case "01":
                requestVO.setUser(userService.toUserVO(userService.getUserById(request.getTargetId())));
                break;
            case "11":
                requestVO.setGroup(groupService.getGroupVO(request.getTargetId(), request.getSenderId()));
                break;
            default:
        }

        // 若 status 为 0 且该用户为接收者，则置为 1 表示已读
        if (request.getSenderId() != userId && request.getReqStatus() == 0) {
            request.setReqStatus(1);
            requestMapper.updateById(request);
        }

        // 返回最后的响应对象
        return requestVO;
    }
}
