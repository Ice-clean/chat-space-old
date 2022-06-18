package top.iceclean.chatspace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.iceclean.chatspace.service.GroupService;
import top.iceclean.logtrace.annotation.EnableLogTrace;
import top.iceclean.logtrace.bean.Logger;

/**
 * @author : Ice'Clean
 * @date : 2022-06-09
 */
@RestController
@RequestMapping("/group")
@EnableLogTrace
public class GroupController {

    @Autowired
    private GroupService groupService;
    private Logger logTrace;

    /**
     * 获取群聊列表
     * @param userId 用户 ID
     * @return 群聊列表
     */
    @GetMapping("/list")
    public Object getGroupList(int userId) {
        return groupService.getGroupList(userId);
    }

    /**
     * 获取群聊中的所有用户
     * @param groupId 群聊 ID
     * @return 所有用户集合
     */
    @GetMapping("/list/user")
    public Object getUserList(int groupId) {
        return groupService.getUserList(groupId);
    }
}
