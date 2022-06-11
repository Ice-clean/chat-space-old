package top.iceclean.chatspace.constant;

/**
 * @author : Ice'Clean
 * @date : 2022-02-07
 */
public enum ResponseStatusEnum {
    /** 标准状态码 */
    OK(0, "ok"),
    BAD_REQUEST(400, "请求失败"),
    INTERNAL_SERVER_ERROR(500, "服务器出错");

    /** 在这里定义错误码 */
//    UNKNOWN_EXCEPTION(500,"未知异常："),
//    SYSTEM_EXCEPTION(500,"系统异常："),
//    AUTHENTICATION_EXCEPTION(500,"认证异常："),
//    AUTHORITY_EXCEPTION(500,"权限异常："),
//    BUSINESS_EXCEPTION(500,"业务异常：");
//    CLASS_CAST_EXCEPTION("GE003","系统异常：类型强制转换异常"),
//    ARITHMETIC_EXCEPTION("GE004","系统异常：算术条件异常"),
//    NULL_POINTER_EXCEPTION("GE005","系统异常：空指针异常"),
//    NUMBER_FORMAT_EXCEPTION("GE006","系统异常：字符串转换为数字异常"),
//    ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION("GE007","系统异常：数组下标越界异常"),
//    NO_SUCH_METHOD_EXCEPTION("GE008","系统异常：方法未找到异常"),
//    NO_CLASS_DEF_FOUND_ERROR("GE009","系统异常：未找到类定义错误"),
//    CLASS_NOT_FOUND_EXCEPTION("GE010","系统异常：找不到类异常"),
//    INDEX_OUT_OF_BOUNDS_EXCEPTION("GE011","系统异常：索引越界异常");

    /**
     * 状态码
     */
    private final int value;

    /**
     * 附加信息
     */
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
