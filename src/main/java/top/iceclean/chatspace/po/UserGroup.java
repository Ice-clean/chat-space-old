package top.iceclean.chatspace.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户群聊映射实体
 * @author : Ice'Clean
 * @date : 2022-05-29
 */
@Data
@TableName("t_user_group")
public class UserGroup {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer groupId;
    private Integer userId;
    private String deleteTime;
    private String createTime;
}
