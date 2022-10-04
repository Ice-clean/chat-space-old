package top.iceclean.chatspace.websocket.share;

import java.util.HashSet;
import java.util.Set;

/**
 * 网格单元属性类
 * 哈希表中存放的是用户 ID 和位置版本号的映射
 * @author : Ice'Clean
 * @date : 2022-09-30
 */
public class GridUnit {
    /** 该网格所属的空间和横纵坐标 */
    private Integer spaceId, x, y;
    /** 该网格所属的区域 ID */
    private Integer zoneId;
    /** 用户集（位置在该网格的用户 ID） */
    private final Set<Integer> userSet;
    /** 视野集（视野覆盖到该网格的用户 ID） */
    private final Set<Integer> visionSet;
    /** 感知集（存在感覆盖到该网格的用户 ID） */
    private final Set<Integer> existSet;

    public GridUnit(int spaceId, int x, int y, int zoneId) {
        this.spaceId = spaceId;
        this.x = x;
        this.y = y;
        this.zoneId = zoneId;
        this.userSet = new HashSet<>();
        this.visionSet = new HashSet<>();
        this.existSet = new HashSet<>();
    }

    /** 向网格中放置用户 */
    public Set<Integer> addUser(int userId) {
        // 有新用户来到该网格
        userSet.add(userId);
        // 网格内的所有视野观察者要要进行汇报（其他用户 尝试发现 该用户）
        return visionSet;
    }

    /** 移除网格中的用户 */
    public Set<Integer> removeUser(int userId) {
        // 用户离开网格
        userSet.remove(userId);
        // 网格内的所有视野观察者要进行汇报（其他用户 移除 该用户）
        return visionSet;
    }

    /** 向网格中方置用户的视野观察者 */
    public Set<Integer> addVision(int userId) {
        // 有新用户的视野观察者加入
        visionSet.add(userId);
        // 视野观察者要扫描所有的用户信息（该用户 尝试发现 其他用户）
        return userSet;
    }

    /** 移除网格中的视野观察者 */
    public Set<Integer> removeVision(int userId) {
        // 用户的视野观察者离开
        visionSet.remove(userId);
        // 视野观察者要先进行汇报（该用户 移除 其他用户）
        return userSet;
    }

    /** 向网格中放置用户存在感官 */
    public void addExist(int userId) {
        // 存在感官仅作为标识存在，不需要其他动作
        existSet.add(userId);
    }

    /** 移除网格中的用户存在感官 */
    public void removeExist(int userId) {
        // 存在感官仅作为标识存在，不需要其他动作
        existSet.remove(userId);
    }

    /** 返回某用户的存在感官是在该网格中 */
    public boolean exist(int userId) {
        return existSet.contains(userId);
    }

    public Integer getSpaceId() {
        return spaceId;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    @Override
    public String toString() {
        return "( " + userSet + " " + visionSet + " " + existSet + " )";
    }
}
