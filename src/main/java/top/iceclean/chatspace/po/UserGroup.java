package top.iceclean.chatspace.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户群聊映射实体
 * @author : Ice'Clean
 * @date : 2022-05-29
 */
@Data
@TableName("t_user_group")
@NoArgsConstructor
public class UserGroup {
    @TableId(value = "map_id", type = IdType.AUTO)
    private Integer mapId;
    private Integer userId;
    private Integer groupId;
    private Integer lastMsgId;
    private String markName;
    private String deleteTime;
    private String createTime;

    public UserGroup(Integer userId, Integer groupId, Integer lastMsgId) {
        this.userId = userId;
        this.groupId = groupId;
        this.lastMsgId = lastMsgId;
    }
}
