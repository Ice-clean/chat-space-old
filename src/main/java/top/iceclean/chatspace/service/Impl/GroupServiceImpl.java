package top.iceclean.chatspace.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import top.iceclean.chatspace.VO.GroupVO;
import top.iceclean.chatspace.VO.UserVO;
import top.iceclean.chatspace.constant.RedisKey;
import top.iceclean.chatspace.constant.ResponseStatusEnum;
import top.iceclean.chatspace.mapper.GroupMapper;
import top.iceclean.chatspace.mapper.UserGroupMapper;
import top.iceclean.chatspace.po.Group;
import top.iceclean.chatspace.po.Response;
import top.iceclean.chatspace.po.UserGroup;
import top.iceclean.chatspace.service.GroupService;
import top.iceclean.chatspace.service.UserService;
import top.iceclean.chatspace.utils.RedisCache;
import top.iceclean.logtrace.annotation.EnableLogTrace;
import top.iceclean.logtrace.bean.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : Ice'Clean
 * @date : 2022-05-29
 */
@Service
@EnableLogTrace
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private UserGroupMapper userGroupMapper;
    @Lazy
    @Autowired
    private UserService userService;
    @Autowired
    private RedisCache redisCache;
    private Logger logTrace;

    @Override
    public Response getGroupList(int userId) {
        // 先获取所有群聊主键
        List<Integer> groupKeyList = getGroupKeyList(userId);
        // 再拿到所有群聊并转化成响应对象
        List<GroupVO> groupList = groupKeyList.stream().map(groupId -> getGroupVO(groupId, userId)).collect(Collectors.toList());
        return new Response(ResponseStatusEnum.OK).addData("groupList", groupList);
    }

    @Override
    public Response getUserList(int groupId) {
        // 获取群聊中所有用户的 ID
        List<Integer> userIdList = userGroupMapper.selectList(new LambdaQueryWrapper<UserGroup>()
                .select(UserGroup::getUserId)
                .isNull(UserGroup::getDeleteTime)
                .eq(UserGroup::getGroupId, groupId))
                .stream().map(UserGroup::getUserId).collect(Collectors.toList());
        // 根据 ID 集合获取所有用户并转化为响应对象
        List<UserVO> userList = userService.getUserList(userIdList)
                .stream().map(user -> userService.toUserVO(user)).collect(Collectors.toList());
        // 最后响应群聊用户列表
        return new Response(ResponseStatusEnum.OK).addData("groupUserList", userList);
    }

    @Override
    public Group getGroupById(int groupId) {
        return groupMapper.selectById(groupId);
    }

    @Override
    public List<Group> getUserManageGroups(int userId) {
        return groupMapper.selectList(new LambdaQueryWrapper<Group>()
                .isNull(Group::getDeleteTime)
                .eq(Group::getCreatorId, userId));
    }

    @Override
    public UserGroup getUserGroup(int userId, int groupId) {
        return userGroupMapper.selectOne(new LambdaQueryWrapper<UserGroup>()
                .isNull(UserGroup::getDeleteTime)
                .eq(UserGroup::getUserId, userId).eq(UserGroup::getGroupId, groupId));
    }

    @Override
    public GroupVO getGroupVO(int groupId, int userId) {
        // 获取群聊对象
        Group group = getGroupById(groupId);
        // 找到用户群聊记录
        UserGroup userGroup = getUserGroup(userId, group.getGroupId());
        return new GroupVO(group, getOnlineNum(group.getGroupId())).setUserGroup(userGroup);
    }

    @Override
    public List<Integer> getGroupUserId(int groupId) {
        // 将所有的群聊映射记录查询出来，再过滤出用户 ID 集合
        return userGroupMapper.selectList(new LambdaQueryWrapper<UserGroup>()
                .select(UserGroup::getUserId)
                .eq(UserGroup::getGroupId, groupId).isNull(UserGroup::getDeleteTime))
                .stream().map(UserGroup::getUserId).collect(Collectors.toList());
    }

    @Override
    public List<Integer> getGroupKeyList(int userId) {
        return userGroupMapper.selectList(new LambdaQueryWrapper<UserGroup>()
                .select(UserGroup::getGroupId)
                .eq(UserGroup::getUserId, userId).isNull(UserGroup::getDeleteTime))
                .stream().map(UserGroup::getGroupId).collect(Collectors.toList());
    }

    @Override
    public void setOnlineNum(int groupId, int change) {
        // 先获取原在线人数
        Object online = redisCache.hashGet(RedisKey.GROUP_ONLINE_HASH, "" + groupId);
        // 更新缓存
        int nowOnline = online == null ? change : Integer.parseInt(online.toString()) + change;
        redisCache.hashSet(RedisKey.GROUP_ONLINE_HASH, "" + groupId, nowOnline);
    }

    @Override
    public int getOnlineNum(int groupId) {
        Object online = redisCache.hashGet(RedisKey.GROUP_ONLINE_HASH, "" + groupId);
        return online == null ? 0 : Integer.parseInt(online.toString());
    }

    @Override
    public void updateLastMsgId(int groupId, int userId, int latestMsgId) {
        // 先获得好友映射，更新完最新消息 ID 再持久化
        UserGroup userGroup = getUserGroup(userId, groupId);
        userGroup.setLastMsgId(latestMsgId);
        userGroupMapper.updateById(userGroup);
    }
}
