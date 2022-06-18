package top.iceclean.chatspace.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.iceclean.chatspace.VO.UserVO;
import top.iceclean.chatspace.constant.SessionType;
import top.iceclean.chatspace.constant.RedisKey;
import top.iceclean.chatspace.constant.ResponseStatusEnum;
import top.iceclean.chatspace.mapper.UserMapper;
import top.iceclean.chatspace.po.Response;
import top.iceclean.chatspace.po.User;
import top.iceclean.chatspace.service.FriendService;
import top.iceclean.chatspace.service.GroupService;
import top.iceclean.chatspace.service.UserService;
import top.iceclean.chatspace.utils.RedisCache;
import top.iceclean.logtrace.annotation.EnableLogTrace;
import top.iceclean.logtrace.bean.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Ice'Clean
 * @date : 2022-05-25
 */
@Service
@EnableLogTrace
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private FriendService friendService;
    @Autowired
    private GroupService groupService;
    private Logger logTrace;

    @Override
    public Response login(String userName, String userPass) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserName, userName));
        boolean success = user != null && userPass.equals(user.getUserPass());
        Response response = new Response().addData("success", success);
        if (success) {
            response.setStatus(ResponseStatusEnum.OK)
                    .setMsg("登录成功").addData("user", toUserVO(user));
        } else {
            response.setStatus(ResponseStatusEnum.LOGIN_FAILED)
                    .setMsg("用户名或密码错误！");
        }
        return response;

    }

    @Override
    public User getUserById(int userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public List<User> getUserList(List<Integer> userIdList) {
        return userMapper.selectList(new LambdaQueryWrapper<User>()
                .isNull(User::getDeleteTime).in(User::getUserId, userIdList));
    }

    @Override
    public UserVO toUserVO(User user) {
        // 添加用户在线状态
        return new UserVO(user, isOnline(user.getUserId()));
    }

    @Override
    public boolean isOnline(int userId) {
        Boolean online = redisCache.getBit(RedisKey.USER_ONLINE_BIT, userId);
        return online != null && online;
    }

    @Override
    public void setOnline(int userId, boolean online) {
        // 改变用户在线状态
        redisCache.setBit(RedisKey.USER_ONLINE_BIT, userId, online);
        // 同时将其所在的所有群聊在线人数 +1/-1
        List<Integer> groupKeyList = groupService.getGroupKeyList(userId);
        for (Integer groupKey : groupKeyList) {
            groupService.setOnlineNum(groupKey, online ? 1 : -1);
        }
    }
}
