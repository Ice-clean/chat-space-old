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
    /** 用户昵称 */
    private String userNick;
    /** 用户头像 */
    private String userAvatar;
    /** 用户是否在线 */
    private Boolean online;

    public UserVO(User user, Boolean online) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.userNick = user.getUserNick();
        this.userAvatar = user.getUserAvatar();
        this.online = online;
    }
}
