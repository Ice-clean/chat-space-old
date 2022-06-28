package top.iceclean.chatspace.VO;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 会话申请列表响应对象
 * @author : Ice'Clean
 * @date : 2022-06-25
 */
@Data
public class SessionRequestListVO {
    /** 用户收到的好友申请列表 */
    private List<SessionRequestVO> friendRequest;
    /** 用户收到的群聊申请列表 */
    private List<SessionRequestVO> groupRequest;
    /** 用户发起的好友申请列表 */
    private List<SessionRequestVO> requestFriend;
    /** 用户发起的群聊申请列表 */
    private List<SessionRequestVO> requestGroup;

    /** 通过收集初始化数据 */
    public SessionRequestListVO(Map<Integer, Map<Integer, List<SessionRequestVO>>> collect) {
        this.friendRequest = collect.get(0) != null ? collect.get(0).get(0) : null;
//        this.groupRequest = collect.get(1) != null ? collect.get(1).get(0) : null;
        this.requestGroup = collect.get(1) != null ? collect.get(1).get(0) : null;
        this.requestFriend = collect.get(0) != null ? collect.get(0).get(1) : null;
//        this.requestGroup = collect.get(1) != null ? collect.get(1).get(1) : null;
        this.groupRequest = collect.get(1) != null ? collect.get(1).get(1) : null;
    }
}
