package top.iceclean.chatspace.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.iceclean.chatspace.DTO.UserDTO;

import javax.validation.constraints.NotNull;

/**
 * 用户类
 * @author : Ice'Clean
 * @date : 2022-05-25
 */
@Data
@TableName("t_user")
@NoArgsConstructor
public class User {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;
    private String userName;
    private String userPass;
    @NotNull
    private String sex;
    private String nickName;
    private String avatar;
    private String email;
    private String freezeTime;
    private String deleteTime;
    private String createTime;
    @TableField(update = "now()")
    private String updateTime;

    public User(UserDTO userDTO) {
        this.userName = userDTO.getUserName();
        this.userPass = userDTO.getUserPass();
        this.sex = userDTO.getSex();
        this.nickName = userDTO.getUserName();
        this.email = userDTO.getEmail();

        // 设置默认头像
        this.avatar = "../img/avatar/default.png";
    }
}
