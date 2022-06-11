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
     * 获取消息列表
     * @param userId 用户 ID
     * @return 消息列表
     */
    Response getMessageList(int userId);

    /**
     * 获取用户在某个接收域的历史消息
     * @param userId 用户 ID
     * @param type 类型
     * @param receiveId 接收域 ID
     * @param page 聊天记录的页数
     * @return 历史消息列表
     */
    Response getChatHistory(int userId, int type, int receiveId, int page);

    /**
     * 将消息对象转化成消息响应对象
     * @param message 消息对象
     * @param userId 当前用户 ID
     * @param shortTime 是否需要缩短时间格式（消息列表需要）
     * @param info 是否有额外信息（消息列表需要）
     * @return 消息响应对象
     */
    MessageVO toMessageVO(Message message, int userId, boolean shortTime, boolean info);

    /**
     * 将消息接收对象持久化，并返回消息实体
     * @param messageDTO 消息接收对象
     * @return 对应的消息实体
     */
    Message saveMessage(MessageDTO messageDTO);
}
