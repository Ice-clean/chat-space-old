package top.iceclean.chatspace.service;

import top.iceclean.chatspace.VO.FriendVO;
import top.iceclean.chatspace.po.Friend;
import top.iceclean.chatspace.po.Response;
import top.iceclean.chatspace.po.User;

import java.util.List;
import java.util.Map;

/**
 * @author : Ice'Clean
 * @date : 2022-05-25
 */
public interface FriendService {

    /**
     * 获取用户好友列表
     * @param userId 用户 ID
     * @return 好友列表
     */
    Response getFriendList(int userId);

    /**
     * 绑定为朋友关系
     * @param senderId 发送者用户 ID
     * @param userId 目标用户 ID
     * @return
     */
    boolean becomeFriends(int senderId, int userId);

    /**
     * 获取用户指定好友 ID 的好友记录
     * @param userId 用户 ID
     * @param friendId 好友 ID
     * @return 好友记录
     */
    Friend getUserFriend(int userId, int friendId);

    /**
     * 通过好友 ID 获取有当前用户特征的响应对象
     * @param friendId 好友 ID
     * @param userId 当前用户 ID
     * @return 好友响应对象
     */
    FriendVO getFriendVO(int friendId, int userId);

    /**
     * 通过好友 ID 获取好友双方的用户 ID
     * @param friendId 好友 ID
     * @return 用户 ID 集合
     */
    List<Integer> getFriendUserId(int friendId);

    /**
     * 获取用户的好友主键列表
     * @param userId 用户 ID
     * @return 好友主键列表
     */
    List<Integer> getFriendKeyList(int userId);

    /**
     * 获取用户的好友主键映射（好友 ID，主键 ID）
     * @param userId 用户 ID
     * @return 好友主键映射
     */
    Map<Integer, Integer> getFriendKeyMap(int userId);

    /**
     * 获取用户的好友 ID 列表
     * @param userId 用户 ID
     * @return 好友 ID 列表
     */
    List<Integer> getFriendIdList(int userId);

    /**
     * 更新用户在好友会话中最后一条消息的 ID 为最新的
     * @param friendId 好友 ID
     * @param userId 用户 ID
     * @param latestMsgId 最新消息 ID
     */
    void updateLastMsgId(int friendId, int userId, int latestMsgId);
}
