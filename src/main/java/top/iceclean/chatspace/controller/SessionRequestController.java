package top.iceclean.chatspace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.iceclean.chatspace.DTO.RequestDTO;
import top.iceclean.chatspace.constant.SessionType;
import top.iceclean.chatspace.service.SessionRequestService;
import top.iceclean.chatspace.validation.annotation.NotSelf;

import javax.validation.Valid;
import java.util.Deque;

/**
 * 会话加入请求
 * @author : Ice'Clean
 * @date : 2022-06-25
 */
@RestController
@RequestMapping("/request")
@Validated
public class SessionRequestController {

    @Autowired
    private SessionRequestService requestService;

    /**
     * 好友申请
     * @return 申请反馈
     */
    @PostMapping("/friend")
    public Object friendRequest(@RequestBody @Valid @NotSelf RequestDTO requestDTO) {
        requestDTO.setType(SessionType.FRIEND);
        return requestService.sendRequest(requestDTO);
    }

    /**
     * 群聊申请
     * @return 申请反馈
     */
    @PostMapping("/group")
    public Object groupRequest(@RequestBody @NotSelf RequestDTO requestDTO) {
        requestDTO.setType(SessionType.GROUP);
        return requestService.sendRequest(requestDTO);
    }

    /**
     * 查看所有相关申请（包括：自己发起的和接受的，群聊和好友的）
     * @param userId 用户 ID
     * @return 已分类的申请列表
     */
    @GetMapping("/list")
    public Object getRequestList(int userId) {
        return requestService.getRequestList(userId);
    }

    /**
     * 获取未读申请记录（分两种）
     * @param userId 目标用户 ID
     * @return 未读申请记录
     */
    @GetMapping("/badge")
    public Object getRequestBadge(int userId) {
        return requestService.getRequestBadge(userId);
    }

    /**
     * 处理申请
     * @param reqId 要处理的申请的序列号
     * @param access 是否同意申请
     * @return 处理结果
     */
    @PatchMapping("/handle")
    public Object handleRequest(int reqId, boolean access) {
        return requestService.handleRequest(reqId, access);
    }
}
