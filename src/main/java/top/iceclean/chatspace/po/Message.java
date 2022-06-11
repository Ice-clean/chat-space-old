package top.iceclean.chatspace.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.iceclean.chatspace.DTO.MessageDTO;

/**
 * 聊天消息实体
 * @author : Ice'Clean
 * @date : 2022-05-25
 */
@Data
@TableName("t_session_message")
@NoArgsConstructor
public class Message {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer sessionId;
    private Integer msgId;
    private Integer senderId;
    private Integer type;
    private String content;
    private String createTime;

    /** 通过消息响应对象构建消息实体 */
    public Message(MessageDTO messageDTO) {
        this.sessionId = messageDTO.getSessionId();
        this.senderId = messageDTO.getSenderId();
        this.type = messageDTO.getType();
        this.content = messageDTO.getContent();
    }
}
