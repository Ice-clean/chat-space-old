package top.iceclean.chatspace.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户类
 * @author : Ice'Clean
 * @date : 2022-05-25
 */
@Data
@TableName("t_user")
public class User {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;
    private String userName;
    private String userPass;
    private String nickName;
    private String avatar;
    private String freezeTime;
    private String deleteTime;
    private String createTime;
    @TableField(update = "now()")
    private String updateTime;
}
