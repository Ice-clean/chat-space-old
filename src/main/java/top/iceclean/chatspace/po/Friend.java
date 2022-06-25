package top.iceclean.chatspace.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Ice'Clean
 * @date : 2022-05-25
 */
@Data
@TableName("t_friend")
@NoArgsConstructor
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

    public Friend(Integer friendId, Integer userId, Integer toUserId) {
        this.friendId = friendId;
        this.userId = userId;
        this.toUserId = toUserId;
    }
}
