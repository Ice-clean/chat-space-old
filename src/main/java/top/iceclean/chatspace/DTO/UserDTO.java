package top.iceclean.chatspace.DTO;

import lombok.Data;

/**
 * 用户接收对象
 * @author : Ice'Clean
 * @date : 2022-05-30
 */
@Data
public class UserDTO {
    /** 登录需要的信息 */
    private String userName;
    private String userPass;

    /** 注册需要的信息 */
    private String sex;
    private String email;
    private String code;

    /** 可修改昵称 */
    private String nickName;
}
