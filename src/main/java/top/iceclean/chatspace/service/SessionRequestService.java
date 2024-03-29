package top.iceclean.chatspace.service;

import top.iceclean.chatspace.DTO.RequestDTO;
import top.iceclean.chatspace.VO.SessionRequestVO;
import top.iceclean.chatspace.constant.SessionType;
import top.iceclean.chatspace.pojo.Response;
import top.iceclean.chatspace.po.SessionRequest;
import top.iceclean.chatspace.validation.annotation.NotSelf;

import javax.validation.Valid;

/**
 * @author : Ice'Clean
 * @date : 2022-06-25
 */
public interface SessionRequestService {

    /**
     * 发送会话申请
     * @param requestDTO 会话申请参数
     * @return 申请反馈
     */
    Response sendRequest(@Valid @NotSelf RequestDTO requestDTO);

    /**
     * 查看所有相关申请（包括：自己发起的和接受的，群聊和好友的）
     * @param userId 用户 ID
     * @return 已分类的申请列表
     */
    Response getRequestList(int userId);

    /**
     * 获取未读申请记录（分四种）
     * @param userId 目标用户 ID
     * @return 未读申请记录
     */
    Response getRequestBadge(int userId);

    /**
     * 处理申请
     * @param reqId 要处理的申请的序列号
     * @param access 是否同意申请
     * @return 处理结果
     */
    Response handleRequest(int reqId, boolean access);

    /**
     * 将会话请求转化为响应对象
     * @param request 会话请求对象
     * @param userId 当前用户 ID
     * @return 会话请求响应对象
     */
    SessionRequestVO toRequestVO(SessionRequest request, int userId);
}
