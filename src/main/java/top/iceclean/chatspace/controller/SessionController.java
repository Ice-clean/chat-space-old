package top.iceclean.chatspace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.iceclean.chatspace.service.MessageService;
import top.iceclean.logtrace.annotation.EnableLogTrace;
import top.iceclean.logtrace.bean.Logger;

/**
 * @author : Ice'Clean
 * @date : 2022-05-25
 */
@RestController
@RequestMapping("session")
@EnableLogTrace
public class SessionController {

    @Autowired
    private MessageService messageService;
    private Logger logTrace;

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
}
