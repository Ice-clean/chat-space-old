package top.iceclean.chatspace.service;

import top.iceclean.chatspace.VO.GroupVO;
import top.iceclean.chatspace.po.Group;
import top.iceclean.chatspace.po.Response;
import top.iceclean.chatspace.po.UserGroup;

import java.util.List;

/**
 * @author : Ice'Clean
 * @date : 2022-05-29
 */
public interface GroupService {

    /**
     * 获取用户所有群聊
     * @param userId 用户 ID
     * @return 群聊列表
     */
    Response getGroupList(int userId);

    /**
     * 通过群聊 ID 获取群聊实体
     * @param groupId 群聊 ID
     * @return 群聊实体
     */
    Group getGroupById(int groupId);

    /**
     * 获取用户某条群聊记录
     * @param userId 用户 ID
     * @param groupId 群聊 ID
     * @return 用户群聊记录
     */
    UserGroup getUserGroup(int userId, int groupId);

    /**
     * 通过群聊 ID 获取有当前用户特征的响应对象
     * @param groupId 群聊 ID
     * @param userId 当前用户 ID
     * @return 对应的响应对象
     */
    GroupVO getGroupVO(int groupId, int userId);

    /**
     * 获取群聊中所有用户 ID
     * @param groupId 群聊 ID
     * @return 所有用户 ID 集合
     */
    List<Integer> getGroupUserId(int groupId);

    /**
     * 获取用户的群聊主键列表
     * @param userId 用户 ID
     * @return 群聊主键列表
     */
    List<Integer> getGroupKeyList(int userId);

    /**
     * 获取群聊在线人数
     * @param groupId 群聊 ID
     * @return 在线人数
     */
    int getOnlineNum(int groupId);

    /**
     * 设置群聊在线人数
     * @param groupId 群聊 ID
     * @param change 在线人数改变数量
     */
    void setOnlineNum(int groupId, int change);
}
