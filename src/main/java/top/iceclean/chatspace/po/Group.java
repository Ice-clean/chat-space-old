package top.iceclean.chatspace.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 群聊实体
 * @author : Ice'Clean
 * @date : 2022-05-29
 */
@Data
@TableName("t_group")
@NoArgsConstructor
public class Group {
    @TableId(value = "group_id", type = IdType.AUTO)
    private Integer groupId;
    private Integer creatorId;
    private String groupName;
    private String avatar;
    private Integer number;
    private String deleteTime;
    private String createTime;
    @TableField(update = "now()")
    private String updateTime;

    public Group(int creatorId, String groupName) {
        this.creatorId = creatorId;
        this.groupName = groupName;
    }
}
