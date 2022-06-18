package top.iceclean.chatspace.constant;

/**
 * @author : Ice'Clean
 * @date : 2022-02-07
 */
public enum ResponseStatusEnum {
    /** 标准状态码 */
    OK(0, "ok"),
    BAD_REQUEST(400, "请求失败"),
    INTERNAL_SERVER_ERROR(500, "服务器出错"),

    /** 错误码 */
    LOGIN_FAILED(1001, "用户名或密码错误");

    /** 状态码 */
    private final int value;

    /** 附加信息 */
    private final String msg;

    ResponseStatusEnum(int value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    public int value() {
        return value;
    }

    public String msg() {
        return msg;
    }
}
