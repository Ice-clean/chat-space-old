package top.iceclean.chatspace.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import top.iceclean.chatspace.VO.UserVO;
import top.iceclean.chatspace.constant.ResponseStatusEnum;
import top.iceclean.chatspace.mapper.FriendMapper;
import top.iceclean.chatspace.po.Friend;
import top.iceclean.chatspace.po.Response;
import top.iceclean.chatspace.po.User;
import top.iceclean.chatspace.service.FriendService;
import top.iceclean.chatspace.service.UserService;
import top.iceclean.logtrace.annotation.EnableLogTrace;
import top.iceclean.logtrace.bean.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : Ice'Clean
 * @date : 2022-05-25
 */
@Service
@EnableLogTrace
public class FriendServiceImpl implements FriendService {
    @Autowired
    private FriendMapper friendMapper;
    @Lazy
    @Autowired
    private UserService userService;
    private Logger logTrace;

    @Override
    public Response getFriendList(int userId) {
        // 先拿到好友 ID 列表
        List<Integer> friendKeyList = getFriendIdList(userId);
        // 拿到所有好友的用户响应对象
        List<UserVO> friendList = friendKeyList.stream().map(friendId -> userService.toUserVO(userService.getUserById(friendId))).collect(Collectors.toList());
        return new Response(ResponseStatusEnum.OK).addData("friendList", friendList);
    }

    @Override
    public List<Integer> getFriendKeyList(int userId) {
        return friendMapper.selectList(new LambdaQueryWrapper<Friend>()
                .eq(Friend::getSenderId, userId).or().eq(Friend::getReceiveId, userId)
                .select(Friend::getFriendId))
                .stream().map(Friend::getFriendId).collect(Collectors.toList());
    }

    @Override
    public Map<Integer, Integer> getFriendKeyMap(int userId) {
        return friendMapper.selectList(new LambdaQueryWrapper<Friend>()
                .eq(Friend::getSenderId, userId).or().eq(Friend::getReceiveId, userId)
                .select(Friend::getFriendId, Friend::getSenderId, Friend::getReceiveId))
                .stream().collect(Collectors.toMap(f -> f.getSenderId() == userId ? f.getReceiveId() : f.getSenderId(), Friend::getFriendId));
    }

    @Override
    public List<Integer> getFriendIdList(int userId) {
        return friendMapper.selectList(new LambdaQueryWrapper<Friend>()
                .eq(Friend::getSenderId, userId).or().eq(Friend::getReceiveId, userId)
                .select(Friend::getSenderId, Friend::getReceiveId))
                .stream().map(f -> f.getSenderId() == userId ? f.getReceiveId() : f.getSenderId()).collect(Collectors.toList());
    }

    @Override
    public List<Integer> getUserIdByFriendId(int friendId) {
        // 找到对应的朋友记录，将发送方和接收方 ID 放入集合中即可
        Friend friend = friendMapper.selectOne(new LambdaQueryWrapper<Friend>()
                .eq(Friend::getFriendId, friendId).isNull(Friend::getDeleteTime));
        return Arrays.asList(friend.getSenderId(), friend.getReceiveId());
    }

    @Override
    public Integer getFriendUserId(int userId, int friendId) {
        Friend friend = friendMapper.selectById(friendId);
        return friend.getSenderId() == userId ? friend.getReceiveId() : friend.getSenderId();
    }
}
