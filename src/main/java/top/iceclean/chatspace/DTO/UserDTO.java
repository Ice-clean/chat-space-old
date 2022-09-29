package top.iceclean.chatspace.DTO;

import lombok.Data;
import top.iceclean.chatspace.validation.annotation.NotUsed;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 用户接收对象
 * @author : Ice'Clean
 * @date : 2022-05-30
 */
@Data
public class UserDTO {
    @NotUsed(message = "用户名已被占用", groups = {Register.class})
    @NotBlank(message = "用户名不能为空或空串", groups = {Login.class})
    private String userName;

    @NotBlank(message = "密码不能为空或空串", groups = {Login.class})
    private String userPass;

    private String sex;

    @NotBlank(message = "邮箱不能为空", groups = {Register.class})
    @Email(message = "邮箱格式错误", groups = {Register.class})
    private String email;

    @NotBlank(message = "验证码不能为空", groups = {Register.class})
    private String code;

    private String nickName;

    /** 分组校验 */
    public interface Register { }
    public interface Login { }
}
