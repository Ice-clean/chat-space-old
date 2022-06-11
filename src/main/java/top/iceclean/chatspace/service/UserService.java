package top.iceclean.chatspace.service;

import top.iceclean.chatspace.VO.UserVO;
import top.iceclean.chatspace.po.Response;
import top.iceclean.chatspace.po.User;

import java.util.List;

/**
 * @author : Ice'Clean
 * @date : 2022-05-25
 */
public interface UserService {

    /**
     * 用户登录
     * @param userName 用户名
     * @param userPass 用户密码
     * @return 是否登录成功
     */
    Response login(String userName, String userPass);

    /**
     * 通过用户 ID 获取用户对象
     * @param userId 用户 ID
     * @return 用户对象
     */
    User getUserById(int userId);

    /**
     * 将用户对象转化成用户响应对象
     * @param user 用户对象
     * @return 用户响应对象
     */
    UserVO toUserVO(User user);

    /**
     * 查询用户是否在线
     * @param userId 用户 ID
     * @return 在线为 true，否则 false
     */
    boolean isOnline(int userId);

    /**
     * 设置用户在线状态
     * @param userId 用户 ID
     * @param online 是否在线
     */
    void setOnline(int userId, boolean online);

    /**
     * 获取指定类型接收域的所有用户 ID
     * @param type 接收域类型
     * @param receiveId 接收域 ID
     * @return 用户 ID 集合
     */
    List<Integer> getUserIdByReceiveId(int type, int receiveId);
}
