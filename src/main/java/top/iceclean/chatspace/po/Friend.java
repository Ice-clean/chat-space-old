package top.iceclean.chatspace.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author : Ice'Clean
 * @date : 2022-05-25
 */
@Data
@TableName("t_friend")
public class Friend {
    @TableId(value = "friend_id", type = IdType.AUTO)
    private Integer friendId;
    private Integer senderId;
    private Integer receiveId;
    private String createTime;
    private String deleteTime;
}
