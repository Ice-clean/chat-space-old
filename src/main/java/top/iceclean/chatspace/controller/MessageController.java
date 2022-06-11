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
@RequestMapping("chat")
@EnableLogTrace
public class MessageController {

    @Autowired
    private MessageService messageService;
    private Logger logTrace;

    /**
     * 获取消息列表
     * @param userId 用户 ID
     * @return 消息列表
     */
    @GetMapping("/list/message")
    public Object getMessageList(int userId) {
        return messageService.getMessageList(userId);
    }

    /**
     * 获取用户在某个接收域的历史消息
     * @param userId 用户 ID
     * @param type 类型
     * @param receiveId 接收域 ID
     * @param page 聊天记录的页数
     * @return 历史消息列表
     */
    @GetMapping("/list/history")
    public Object getChatHistory(int userId, int type, int receiveId, int page) {
        return messageService.getChatHistory(userId, type, receiveId, page);
    }
}
