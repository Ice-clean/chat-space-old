package top.iceclean.chatspace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.iceclean.chatspace.DTO.UserDTO;
import top.iceclean.chatspace.constant.RedisKey;
import top.iceclean.chatspace.constant.ResponseStatusEnum;
import top.iceclean.chatspace.pojo.Response;
import top.iceclean.chatspace.po.User;
import top.iceclean.chatspace.service.UserService;
import top.iceclean.chatspace.utils.MailUtils;
import top.iceclean.chatspace.utils.RedisCache;

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.*;

/**
 * @author : Ice'Clean
 * @date : 2022-05-25
 */
@RestController
@RequestMapping("user")
@EnableAsync
@Validated
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private MailUtils mailUtils;

    @PostMapping("/login")
    public Object login(@Validated(UserDTO.Login.class) UserDTO userDTO) {
        return userService.login(userDTO.getUserName() != null ?
                userDTO.getUserName() : userDTO.getEmail(), userDTO.getUserPass());
    }

    /**
     * 通过邮箱发送验证码
     * @param userDTO 用户请求体（用户名 + 邮箱）
     * @return 验证码
     * @exception MessagingException 邮件发送异常，交给全局处理器
     */
    @PostMapping("/code")
    public Object sendCode(@Validated(UserDTO.Register.class) UserDTO userDTO) throws MessagingException {
        // 随机生成六位数验证码
        int code = new Random(Instant.now().toEpochMilli()).nextInt(899999) + 100000;
        // 将验证码保存到缓存中（用户名、邮箱以及验证码一起校验，不允许其中一个有改动，全都放入缓存等待检验），五分钟内有效
        redisCache.hashSet(RedisKey.USER_CODE_HASH, userDTO.getUserName(), userDTO.getEmail() + code, 5 * 60);
        // 发送邮件
        mailUtils.sendHtmlMail("ChatSpace 注册码", "<h2>欢迎加入 ChatSpace ! </h2>" +
                "请求注册的用户 " + userDTO.getUserName() + "<br>" +
                "验证码为：" + code + "，五分钟内有效", userDTO.getEmail());
        return new Response(ResponseStatusEnum.OK).setMsg("发送验证码成功");
    }

    @PostMapping("/register")
    public Object register(@Validated(UserDTO.Login.class) UserDTO userDTO) {
        return userService.register(userDTO);
    }

    /**
     * 通过用户 ID 获取用户
     * @param userId 用户 ID
     * @return 用户详细信息
     */
    @GetMapping("/id/{userId}")
    public Object getUser(@PathVariable int userId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            return new Response(ResponseStatusEnum.OK).setData(userService.toUserVO(user));
        }
        return new Response(ResponseStatusEnum.USER_NOT_EXIST);
    }

    /**
     * 上传头像
     * @param avatar 头像文件数据
     * @return 上传完毕的头像路径
     */
    @PostMapping("/avatar")
    public Object uploadAvatar(MultipartFile avatar) {
        return userService.uploadAvatar(avatar);
    }

    /**
     * 修改用户信息
     * @param userDTO 有效字段：性别、昵称
     * @return 是否修改成功
     */
    @PatchMapping("/info")
    public Object updateInfo(@Validated UserDTO userDTO) {
        return userService.updateInfo(userDTO);
    }

    /**
     * 更改密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否更改成功
     */
    @PatchMapping("/password")
    public Object updatePassword(String oldPassword, String newPassword) {
        return userService.updatePassword(oldPassword, newPassword);
    }

    /**
     * 模糊搜索用户
     * 搜索范围包括用户 ID、用户名、昵称和邮箱
     * 用户 ID 和邮箱为精确搜索
     * 用户名和昵称为模糊收缩
     * @param key 搜索的关键字
     * @return 符合条件的用户列表
     */
    @GetMapping("/search")
    public Object searchUser(String key) {
        return userService.searchUser(key);
    }
}
