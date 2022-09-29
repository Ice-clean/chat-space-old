package top.iceclean.chatspace.service;

import org.springframework.web.multipart.MultipartFile;
import top.iceclean.chatspace.DTO.UserDTO;
import top.iceclean.chatspace.VO.UserVO;
import top.iceclean.chatspace.pojo.Response;
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
     * 用户注册
     * @param userDTO 用户数据对象
     * @return 是否注册成功
     */
    Response register(UserDTO userDTO);

    /**
     * 修改当前用户的头像
     * @param avatar 头像文件数据
     * @return 上传的头像路径
     */
    Response uploadAvatar(MultipartFile avatar);

    /**
     * 修改用户信息
     * @param userDTO 有效字段：性别、昵称
     * @return 是否修改成功
     */
    Response updateInfo(UserDTO userDTO);

    /**
     * 更改密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否更改成功
     */
    Response updatePassword(String oldPassword, String newPassword);

    /**
     * 模糊搜索用户
     * 搜索范围包括用户 ID、用户名、昵称和邮箱
     * 用户 ID 和邮箱为精确搜索
     * 用户名和昵称为模糊收缩
     * @param key 搜索的关键字
     * @return 符合条件的用户列表
     */
    Response searchUser(String key);

    /**
     * 获取当前用户信息
     * @return 当前用户信息实体
     */
    User getCurrentUser();

    /**
     * 通过用户 ID 获取用户对象
     * @param userId 用户 ID
     * @return 用户对象
     */
    User getUserById(int userId);

    /**
     * 根据用户名获取用户信息
     * @param userName 用户名
     * @return 用户信息
     */
    User getUserByUserName(String userName);

    /**
     * 根据用户 ID 列表获取所有用户
     * @param userIdList 用户 ID 列表
     * @return 用户集合
     */
    List<User> getUserList(List<Integer> userIdList);

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
}
