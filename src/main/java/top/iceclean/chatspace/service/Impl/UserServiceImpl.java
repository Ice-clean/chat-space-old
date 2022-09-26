package top.iceclean.chatspace.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.iceclean.chatspace.DTO.UserDTO;
import top.iceclean.chatspace.VO.UserVO;
import top.iceclean.chatspace.constant.FileConst;
import top.iceclean.chatspace.constant.RedisKey;
import top.iceclean.chatspace.constant.ResponseStatusEnum;
import top.iceclean.chatspace.mapper.UserMapper;
import top.iceclean.chatspace.pojo.GlobalException;
import top.iceclean.chatspace.pojo.Response;
import top.iceclean.chatspace.po.User;
import top.iceclean.chatspace.pojo.UserAuthority;
import top.iceclean.chatspace.service.GroupService;
import top.iceclean.chatspace.service.UserService;
import top.iceclean.chatspace.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author : Ice'Clean
 * @date : 2022-05-25
 */
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private GroupService groupService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Response login(String userName, String userPass) {
        // AuthenticationManager authenticate 进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, userPass);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        // 如果认证没通过，返回失败
        if (Objects.isNull(authenticate)) {
            return new Response().setStatus(ResponseStatusEnum.LOGIN_FAILED)
                    .setMsg("用户名或密码错误！")
                    .addData("success", false);
        }

        // 把完整的用户信息存入 redis，userId 作为 key
        UserAuthority userAuthority = (UserAuthority) authenticate.getPrincipal();
        String userId = userAuthority.getUser().getUserId().toString();
        redisCache.setCacheObject(RedisKey.USER_LOGIN + userId, userAuthority, 3, TimeUnit.HOURS);

        // 使用 userId 生成一个 JWT 返回
        return new Response().setStatus(ResponseStatusEnum.OK)
                .setMsg("登录成功")
                .addData("success", true)
                .addData("token", JwtUtils.createJWT(userId))
                .addData("user", toUserVO(userAuthority.getUser()));
    }

    @Override
    public Response register(UserDTO userDTO) {
        // 先查询缓存中的验证码，为空或不匹配则无效
        Object code = redisCache.hashGet(RedisKey.USER_CODE_HASH, userDTO.getUserName());
        if (code == null || !(userDTO.getEmail() + userDTO.getCode()).equals(code.toString())) {
            return new Response(ResponseStatusEnum.CODE_INVALID);
        }

        // 将密码加密，并设置牧人头像
        userDTO.setUserPass(Md5Utils.encode(userDTO.getUserPass()));

        // 验证码有效则执行注册
        if (userMapper.insert(new User(userDTO)) != 1) {
            return new Response(ResponseStatusEnum.DATABASE_ERROR);
        }

        // 成功则查询用户的详细信息并返回
        User user = getUserByUserName(userDTO.getUserName());
        return new Response(ResponseStatusEnum.OK)
                .setMsg("注册成功")
                .setData(toUserVO(user));
    }

    @Override
    public Response uploadAvatar(int userId, MultipartFile avatar) {
        // 将用户查询出来
        User user = getUserById(userId);

        // 上传头像（以用户ID-当前时间-头像大小命名）
        String extentName = avatar.getOriginalFilename();
        extentName = Objects.requireNonNull(extentName).substring(extentName.lastIndexOf("."));
        String avatarName = String.format("%d_%s_%d%s", userId, DateUtils.getTimeCompact(),
                avatar.getSize(), extentName);
        FileConst.Code code = FileUtils.upload(avatar, FileConst.AVATAR_PATH, avatarName);

        // 上传成功时修改头像
        if (code == FileConst.Code.UPLOAD_SUCCESS) {
            user.setAvatar("../" + FileConst.AVATAR_PATH + avatarName);
            userMapper.updateById(user);
            return new Response(ResponseStatusEnum.OK)
                    .setMsg(code.getMsg())
                    .addData("avatarName", avatarName);
        }

        // 上传失败时返回错误信息
        return new Response(ResponseStatusEnum.INTERNAL_SERVER_ERROR).setMsg(code.getMsg());
    }

    @Override
    public Response updateInfo(int userId, UserDTO userDTO) {
        // 拿到用户信息
        User user = getUserById(userId);
        if (user == null) {
            return new Response(ResponseStatusEnum.USER_NOT_EXIST);
        }

        // 修改性别
        if (userDTO.getSex() != null) {
            user.setSex(userDTO.getSex());
        }

        // 修改昵称
        if (userDTO.getNickName() != null) {
            user.setNickName(userDTO.getNickName());
        }

        // 执行修改并返回结果
        if (userMapper.updateById(user) == 1) {
            return new Response(ResponseStatusEnum.OK).setMsg("更新信息成功");
        }
        return new Response(ResponseStatusEnum.DATABASE_ERROR);
    }

    @Override
    public Response updatePassword(int userId, String oldPassword, String newPassword) {
        // 拿到用户
        User user = getUserById(userId);
        if (user == null) {
            return new Response(ResponseStatusEnum.USER_NOT_EXIST);
        }

        // 匹配旧密码
        if (!user.getUserPass().equals(Md5Utils.encode(oldPassword))) {
            return new Response(ResponseStatusEnum.PASSWORD_INVALID);
        }

        // 匹配成功，则给予修改
        user.setUserPass(Md5Utils.encode(newPassword));
        if (userMapper.updateById(user) == 1) {
            return new Response(ResponseStatusEnum.OK).setMsg("修改密码成功");
        }
        return new Response(ResponseStatusEnum.DATABASE_ERROR);
    }

    Pattern pattern = Pattern.compile("^\\d+$");
    @Override
    public Response searchUser(String key) {
        // 判断是否为数值以启用 ID 的精确搜索
        boolean searchId = pattern.matcher(key).matches();
        System.out.println(searchId);
        // 判断是否为邮箱以启用邮箱的精确搜索
        boolean searchEmail = key.contains("@");
        // 进行模糊搜素，并转化为响应对象
        List<UserVO> collect = userMapper.selectList(new LambdaQueryWrapper<User>()
                .isNull(User::getDeleteTime)
                .eq(searchId, User::getUserId, key)
                .eq(searchEmail, User::getEmail, key)
                .or(searchId||searchEmail).like(User::getUserName, key)
                .or().like(User::getNickName, key))
                .stream().map(this::toUserVO).collect(Collectors.toList());
        // 最后返回搜索出来的用户列表
        return new Response(ResponseStatusEnum.OK)
                .addData("total", collect.size())
                .addData("result", collect);
    }

    @Override
    public User getUserById(int userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public User getUserByUserName(String userName) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserName, userName));
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

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        // 查询用户信息
        User user = getUserByUserName(userName);

        //如果没有查询到用户就抛出异常，认证失败
        if(Objects.isNull(user)){
            throw new GlobalException(ResponseStatusEnum.AUTHENTICATION_ERROR, "用户名或密码错误");
        }

        // 封装用户信息
        List<String> permissions = new ArrayList<>();
        permissions.add("User");
        return new UserAuthority(user, permissions);
    }
}
