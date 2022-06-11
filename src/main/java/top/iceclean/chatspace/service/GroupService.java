package top.iceclean.chatspace.service;

import top.iceclean.chatspace.VO.GroupVO;
import top.iceclean.chatspace.po.Group;
import top.iceclean.chatspace.po.Response;

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
     * 获取用户的群聊主键列表
     * @param userId 用户 ID
     * @return 群聊主键列表
     */
    List<Integer> getGroupKeyList(int userId);

    /**
     * 获取群聊中所有用户 ID
     * @param groupId 群聊 ID
     * @return 所有用户 ID 集合
     */
    List<Integer> getUserIdByGroupId(int groupId);

    /**
     * 通过群聊 ID 获取群聊实体
     * @param groupId 群聊 ID
     * @return 群聊实体
     */
    Group getGroupById(int groupId);

    /**
     * 将群聊对象转化成响应对象
     * @param group 群聊对象
     * @return 对应的响应对象
     */
    GroupVO toGroupVO(Group group);

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
