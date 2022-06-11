package top.iceclean.chatspace.service;

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
     * 通过好友 ID 获取好友双方的用户 ID
     * @param friendId 好友 ID
     * @return 用户 ID 集合
     */
    List<Integer> getUserIdByFriendId(int friendId);

    /**
     * 获取指定用户指定好友 ID 的用户ID
     * @param userId 用户 ID
     * @param friendId 朋友 ID
     * @return 朋友用户 ID
     */
    Integer getFriendUserId(int userId, int friendId);
}
