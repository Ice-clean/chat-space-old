package top.iceclean.chatspace.service;

import top.iceclean.chatspace.VO.SessionVO;
import top.iceclean.chatspace.constant.SessionType;
import top.iceclean.chatspace.po.Session;

import java.util.List;

/**
 * @author : Ice'Clean
 * @date : 2022-06-11
 */
public interface SessionService {

    /**
     * 创建会话
     * @param type 会话类型
     * @param targetId 会话的目标 ID
     * @return 新建会话对象
     */
    Session createSession(SessionType type, int targetId);

    /**
     * 获取用户所有的会话 ID
     * @param userId 用户 ID
     * @return 会话 ID 集合
     */
    List<Integer> getSessionIdList(int userId);

    /**
     * 通过会话 ID 获取简单的会话响应对象
     * @param sessionId 会话 ID
     * @return 会话响应对象
     */
    SessionVO getSessionVO(int sessionId);

    /**
     * 通过会话 ID 和当前用户 ID 获取详细的会话响应对象
     * @param sessionId 会话 ID
     * @param userId 用户 ID
     * @return 详细的会话响应对象
     */
    SessionVO getSessionVO(int sessionId, int userId);

    /**
     * 获取指定会话的所有用户 ID
     * @param sessionId 会话 ID
     * @return 用户 ID 集合
     */
    List<Integer> getSessionUserId(int sessionId);

    /**
     * 更新用户在会话中最后一条消息的 ID 为最新的
     * @param sessionId 会话 ID
     * @param userId 用户 ID
     */
    void updateLastMsgId(int sessionId, int userId);
}
