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
    @TableId(value = "map_id", type = IdType.AUTO)
    private Integer mapId;
    private Integer friendId;
    private Integer userId;
    private Integer toUserId;
    private Integer lastMsgId;
    private String markName;
    private String createTime;
    private String deleteTime;
}
