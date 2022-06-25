package top.iceclean.chatspace.VO;

import lombok.Data;

import java.util.Map;
import java.util.Optional;

/**
 * 未读申请记录响应对象
 * @author : Ice'Clean
 * @date : 2022-06-25
 */
@Data
public class RequestBadgeVO {
    /** 所有的未读申请 */
    private Integer total;
    /** 未读的好友申请 */
    private Integer friend;
    /** 未读的群聊申请 */
    private Integer group;

    /** 通过收集结果设置数据 */
    public RequestBadgeVO(Map<Integer, Long> collect) {
        if (collect != null) {
            this.friend = Optional.ofNullable(collect.get(0)).orElse(0L).intValue();
            this.group = Optional.ofNullable(collect.get(1)).orElse(0L).intValue();
            this.total = this.friend + this.group;
        }
    }
}
