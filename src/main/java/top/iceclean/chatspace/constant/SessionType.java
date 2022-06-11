package top.iceclean.chatspace.constant;

/**
 * 接收方类型
 * @author : Ice'Clean
 * @date : 2022-05-29
 */
public enum SessionType {
    /** 好友 */
    FRIEND(0),
    GROUP(1);

    private final int value;
    SessionType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
