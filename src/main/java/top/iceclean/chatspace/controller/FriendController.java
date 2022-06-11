package top.iceclean.chatspace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.iceclean.chatspace.service.FriendService;
import top.iceclean.logtrace.annotation.EnableLogTrace;
import top.iceclean.logtrace.bean.Logger;

/**
 * @author : Ice'Clean
 * @date : 2022-06-09
 */
@RestController
@RequestMapping("/friend")
@EnableLogTrace
public class FriendController {

    @Autowired
    private FriendService friendService;

    private Logger logTrace;

    /**
     * 获取用户的好友列表
     * @param userId 用户 ID
     * @return 好友列表
     */
    @GetMapping("/list")
    public Object getFriendList(int userId) {
        return friendService.getFriendList(userId);
    }
}
