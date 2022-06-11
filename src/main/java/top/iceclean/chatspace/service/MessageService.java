package top.iceclean.chatspace.service;

import top.iceclean.chatspace.DTO.MessageDTO;
import top.iceclean.chatspace.VO.MessageVO;
import top.iceclean.chatspace.po.Message;
import top.iceclean.chatspace.po.Response;

/**
 * @author : Ice'Clean
 * @date : 2022-05-25
 */
public interface MessageService {

    /**
     * 获取会话消息列表
     * @param userId 用户 ID
     * @return 会话列表
     */
    Response getSessionMessageList(int userId);

    /**
     * 获取用户在某个接收域的历史消息
     * @param userId 用户 ID
     * @param sessionId 会话 ID
     * @param page 聊天记录的页数
     * @return 历史消息列表
     */
    Response getChatHistory(int userId, int sessionId, int page);

    /**
     * 将消息对象转化成消息响应对象
     * @param message 消息对象
     * @param userId 当前用户 ID
     * @param info 是否需要详细信息（会话消息列表需要）
     * @return 消息响应对象
     */
    MessageVO toMessageVO(Message message, int userId, boolean info);

    /**
     * 将消息接收对象持久化，并返回消息实体
     * @param messageDTO 消息接收对象
     * @return 对应的消息实体
     */
    Message saveMessage(MessageDTO messageDTO);

    /**
     * 获取指定会话最后一条消息的 ID
     * @param sessionId 会话 ID
     * @return 最后一条消息 ID
     */
    int getLastMsgId(int sessionId);
}
