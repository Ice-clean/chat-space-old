package top.iceclean.chatspace.VO;

import lombok.Data;
import top.iceclean.chatspace.po.SessionRequest;

/**
 * 会话请求响应对象
 * 通过 type 和 kind 确定会话请求的详情
 * (0, 0)：表示用户收到的好友申请，user 值为申请者，group 值不生效
 * (1, 0)：表示用户收到的群聊申请，user 值为申请者，group 值为目标群聊
 * (0, 1)：表示用户发起好友申请，user 值为目标好友，group 值不生效
 * (1, 1)：表示用户发起的群聊申请，user 值不生效，group 值为目标群聊

 * @author : Ice'Clean
 * @date : 2022-06-25
 */
@Data
public class SessionRequestVO {
    /** 申请序列号 */
    private Integer reqId;
    /** 会话类型（0 好友，1 群聊） */
    private Integer type;
    /** 请求类型（0 用户收到的申请，1 用户发起的申请） */
    private Integer kind;
    /** 用户对象 */
    private UserVO user;
    /** 群聊对象 */
    private GroupVO group;
    /** 请求来源 */
    private String reqSrc;
    /** 请求备注 */
    private String reqRemark;
    /** 请求状态（0 未读，1 已读，2 接受，3 拒绝）对于发送方，0 和 1 都是等待验证状态 */
    private Integer reqStatus;
    /** 请求发送时间 */
    private String sendTime;

    public SessionRequestVO(SessionRequest request, int userId) {
        this.reqId = request.getReqId();
        this.type = request.getType();
        this.kind = userId == request.getSenderId() ? 0 : 1;
        this.reqSrc = request.getReqSrc();
        this.reqRemark = request.getReqRemark();
        this.reqStatus = request.getReqStatus();
        this.sendTime = request.getCreateTime();
    }

    public SessionRequestVO setUser(UserVO user) {
        this.user = user;
        return this;
    }

    public SessionRequestVO setGroup(GroupVO group) {
        this.group = group;
        return this;
    }
}
