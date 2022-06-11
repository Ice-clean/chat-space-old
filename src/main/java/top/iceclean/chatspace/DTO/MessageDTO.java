package top.iceclean.chatspace.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 聊天消息接收对象
 * @author : Ice'Clean
 * @date : 2022-05-29
 */
@Data
@AllArgsConstructor
public class MessageDTO {
    /** 消息所属会话 */
    private Integer sessionId;

    /** 消息发送者 ID */
    private Integer senderId;

    /** 消息类型 */
    private Integer type;

    /** 消息主体内容 */
    private String content;
}
