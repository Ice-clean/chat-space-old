package top.iceclean.chatspace.pojo;

import lombok.Getter;
import lombok.Setter;
import top.iceclean.chatspace.constant.ResponseStatusEnum;

/**
 *
 * @date : 2022-06-25
 * @author : Ice'Clean
 */
@Getter
@Setter
public class GlobalException extends RuntimeException{

    /** 状态码 */
    private ResponseStatusEnum status;
    /** 额外信息 */
    private String extraMessage;

    /**
     * 自定义异常构造器
     * @param status 自定义异常类型
     * @param message   具体异常描述
     */
    public GlobalException(ResponseStatusEnum status, String message){
        super(status.msg() + message);
        this.status = status;
        this.extraMessage = message;
    }
}
