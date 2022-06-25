package top.iceclean.chatspace.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.iceclean.chatspace.constant.ResponseStatusEnum;
import top.iceclean.chatspace.pojo.GlobalException;
import top.iceclean.chatspace.pojo.Response;
import top.iceclean.logtrace.bean.LogTrace;

/**
 * 自定义全局异常处理器
 * @date : 2022-06-25
 * @author : Ice'Clean
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private LogTrace logTrace;

    /**
     * 捕获所有用户自定义异常
     * @param exception 用户自定义异常
     * @return 异常及请求信息
     */
    @ExceptionHandler(value = GlobalException.class)
    public Response handleGlobalException(GlobalException exception){
        return new Response().setStatus(exception.getStatus())
                .setMsg(exception.getStatus().msg() + exception.getExtraMessage());
    }

    /**
     * 捕获其他类型的异常
     * @param exception 其他类型的异常
     * @return 异常及请求信息
     */
    @ExceptionHandler(value = Exception.class)
    public Response handleException(Exception exception) {
        log.error("全局异常处理器启动:捕获其他异常");
        log.error(exception.toString());

        if (exception instanceof AuthenticationException) {
            // SpringSecurity 的剩余认证异常需要额外捕获
            return new Response().setStatus(ResponseStatusEnum.AUTHENTICATION_ERROR)
                    .setMsg(ResponseStatusEnum.AUTHENTICATION_ERROR.msg() + exception.getMessage());
        } else if (exception instanceof AccessDeniedException) {
            // SpringSecurity 的剩余权限异常需要额外捕获
            return new Response().setStatus(ResponseStatusEnum.AUTHORITY_ERROR)
                    .setMsg(ResponseStatusEnum.AUTHENTICATION_ERROR.msg() + exception.getMessage());
        }

        //系统未知异常（剩余异常）捕获
        return new Response().setStatus(ResponseStatusEnum.UNKNOWN_ERROR);
    }
}
