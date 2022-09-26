package top.iceclean.chatspace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.iceclean.chatspace.constant.ResponseStatusEnum;
import top.iceclean.chatspace.pojo.Response;
import top.iceclean.chatspace.service.MessageService;
import top.iceclean.chatspace.service.SessionService;

/**
 * @author : Ice'Clean
 * @date : 2022-05-25
 */
@RestController
@RequestMapping("session")
public class SessionController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private SessionService sessionService;

    /**
     * 获取会话列表
     * @param userId 用户 ID
     * @return 消息列表
     */
    @GetMapping("/list")
    public Object getSessionList(int userId) {
        return messageService.getSessionMessageList(userId);
    }

    /**
     * 获取用户在某个会话的历史消息
     * @param userId 用户 ID
     * @param sessionId 会话 ID
     * @param page 聊天记录的页数
     * @return 历史消息列表
     */
    @GetMapping("/history")
    public Object getChatHistory(int userId, int sessionId, int page) {
        return messageService.getChatHistory(userId, sessionId, page);
    }

    /**
     * 通过会话 ID 和当前用户 ID 获取会话详情
     * @param sessionId 会话 ID
     * @param userId 用户 ID
     * @return 会话响应对象
     */
    @GetMapping("/{sessionId}")
    public Object getSession(@PathVariable int sessionId, int userId) {
        return new Response(ResponseStatusEnum.OK)
                .addData("session", sessionService.getSessionVO(sessionId, userId));
    }
}
