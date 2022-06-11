package top.iceclean.chatspace.VO;

import lombok.Data;
import top.iceclean.chatspace.po.Group;

/**
 * 群聊响应对象
 * @author : Ice'Clean
 * @date : 2022-05-29
 */
@Data
public class GroupVO {
    /** 群聊 ID */
    private Integer groupId;
    /** 群聊名称 */
    private String name;
    /** 群聊头像 */
    private String avatar;
    /** 群聊公告 */
    private String notice;
    /** 群聊人数 */
    private String number;
    /** 群聊在线人数 */
    private Integer onlineNum;

    public GroupVO(Group group, int onlineNum) {
        this.groupId = group.getGroupId();
        this.name = group.getName();
        this.avatar = group.getAvatar();
        this.notice = group.getNotice();
        this.number = group.getNumber();
        this.onlineNum = onlineNum;
    }
}
