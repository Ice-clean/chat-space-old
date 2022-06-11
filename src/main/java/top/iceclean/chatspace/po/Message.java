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
@TableName("t_message")
@NoArgsConstructor
public class Message {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer type;
    private Integer senderId;
    private Integer receiveId;
    private String content;
    private Boolean isRead;
    private String createTime;

    /** 通过消息响应对象构建消息实体 */
    public Message(MessageDTO messageDTO) {
        this.type = messageDTO.getType();
        this.senderId = messageDTO.getSenderId();
        this.receiveId = messageDTO.getReceiveId();
        this.content = messageDTO.getContent();
    }
}
