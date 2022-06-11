package top.iceclean.chatspace.VO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 用户上线状态改变响应对象
 * @author : Ice'Clean
 * @date : 2022-06-08
 */
@Data
@AllArgsConstructor
public class UserOnlineVO {
    /** 用户 ID */
    private Integer userId;
    /** 上线状态 */
    private Boolean online;
    /** 发生状态变化的私聊接收域 */
    private Integer friend;
    /** 发生状态变化的群聊接收域 */
    private List<Integer> group;
}
