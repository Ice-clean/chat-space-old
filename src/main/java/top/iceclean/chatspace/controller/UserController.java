package top.iceclean.chatspace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.iceclean.chatspace.DTO.UserDTO;
import top.iceclean.chatspace.constant.ResponseStatusEnum;
import top.iceclean.chatspace.po.Response;
import top.iceclean.chatspace.po.User;
import top.iceclean.chatspace.service.UserService;
import top.iceclean.logtrace.annotation.EnableLogTrace;
import top.iceclean.logtrace.bean.Logger;

/**
 * @author : Ice'Clean
 * @date : 2022-05-25
 */
@RestController
@RequestMapping("user")
@EnableLogTrace
public class UserController {

    @Autowired
    private UserService userService;
    private Logger logTrace;

    @PostMapping("/login")
    public Object login(UserDTO userDTO) {
        System.out.println(userDTO.getUserName() + " " + userDTO.getUserPass());
        return userService.login(userDTO.getUserName(), userDTO.getUserPass());
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
}
