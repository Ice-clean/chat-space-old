package top.iceclean.chatspace.VO;

import lombok.Data;
import top.iceclean.chatspace.po.User;

/**
 * 用户响应对象
 * @author : Ice'Clean
 * @date : 2022-05-26
 */
@Data
public class UserVO {
    /** 用户 ID */
    private Integer userId;
    /** 用户名 */
    private String userName;
    /** 用户性别 */
    private String sex;
    /** 用户昵称 */
    private String nickName;
    /** 用户邮箱 */
    private String email;
    /** 用户头像 */
    private String avatar;
    /** 用户是否在线 */
    private Boolean online;

    public UserVO(User user, boolean online) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.sex = user.getSex();
        this.nickName = user.getNickName();
        this.avatar = user.getAvatar();
        this.email = user.getEmail();
        this.online = online;
    }
}
