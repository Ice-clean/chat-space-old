package top.iceclean.chatspace.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import top.iceclean.chatspace.VO.FriendVO;
import top.iceclean.chatspace.VO.UserVO;
import top.iceclean.chatspace.constant.ResponseStatusEnum;
import top.iceclean.chatspace.mapper.FriendMapper;
import top.iceclean.chatspace.po.Friend;
import top.iceclean.chatspace.pojo.Response;
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
    @Autowired
    private UserService userService;
    private Logger logTrace;

    @Override
    public Response getFriendList(int userId) {
        // 获取好友用户 ID 列表
        List<Integer> friendIdList = getFriendIdList(userId);
        // 然后拿到所有好友的用户响应对象并返回
        List<UserVO> friendList = friendIdList.stream()
                .map(friendId -> userService.toUserVO(userService.getUserById(friendId)))
                .collect(Collectors.toList());
        return new Response(ResponseStatusEnum.OK).addData("friendList", friendList);
    }

    @Override
    public boolean becomeFriends(int senderId, int userId) {
        // 拿到最大的朋友 ID
        Integer maxFriendId = friendMapper.selectOne(new QueryWrapper<Friend>().select("max(friend_id)")).getFriendId();
        // 建立双向的映射关系，并返回是否执行成功
        return friendMapper.insert(new Friend(maxFriendId + 1, senderId, userId)) == 1;
    }

    @Override
    public Friend getUserFriend(int userId, int friendId) {
        return friendMapper.selectOne(new LambdaQueryWrapper<Friend>()
                .isNull(Friend::getDeleteTime)
                .eq(Friend::getUserId, userId).eq(Friend::getFriendId, friendId));
    }

    @Override
    public FriendVO getFriendVO(int friendId, int userId) {
        // 获取用户好友记录
        Friend friend = getUserFriend(userId, friendId);
        // 获取好友用户对象
        User friendUser = userService.getUserById(friend.getToUserId());
        return new FriendVO(userService.toUserVO(friendUser), friend);
    }

    @Override
    public List<Integer> getFriendUserId(int friendId) {
        // 找到对应的朋友记录，将发送方和接收方 ID 放入集合中即可
        Friend friend = friendMapper.selectOne(new LambdaQueryWrapper<Friend>()
                .isNull(Friend::getDeleteTime)
                .eq(Friend::getFriendId, friendId).last("limit 1"));
        return Arrays.asList(friend.getUserId(), friend.getToUserId());
    }

    @Override
    public List<Integer> getFriendKeyList(int userId) {
        return friendMapper.selectList(new LambdaQueryWrapper<Friend>()
                .select(Friend::getFriendId)
                .eq(Friend::getUserId, userId).isNull(Friend::getDeleteTime))
                .stream().map(Friend::getFriendId).collect(Collectors.toList());
    }

    @Override
    public Map<Integer, Integer> getFriendKeyMap(int userId) {
        // 拿到好友主键 ID 和好友用户 ID，将好友用户 ID 映射到好友主键 ID
        return friendMapper.selectList(new LambdaQueryWrapper<Friend>()
                .select(Friend::getFriendId, Friend::getToUserId)
                .eq(Friend::getUserId, userId).isNull(Friend::getDeleteTime))
                .stream().collect(Collectors.toMap(Friend::getToUserId, Friend::getFriendId));
    }

    @Override
    public List<Integer> getFriendIdList(int userId) {
        return friendMapper.selectList(new LambdaQueryWrapper<Friend>()
                .select(Friend::getToUserId)
                .eq(Friend::getUserId, userId).isNull(Friend::getDeleteTime))
                .stream().map(Friend::getToUserId).collect(Collectors.toList());
    }

    @Override
    public void updateLastMsgId(int friendId, int userId, int latestMsgId) {
        // 先获得好友映射，更新完最新消息 ID 再持久化
        Friend friend = getUserFriend(userId, friendId);
        friend.setLastMsgId(latestMsgId);
        friendMapper.updateById(friend);
    }
}
