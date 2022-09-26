package top.iceclean.chatspace.handler;

import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.iceclean.chatspace.constant.ResponseStatusEnum;
import top.iceclean.chatspace.pojo.GlobalException;
import top.iceclean.chatspace.pojo.Response;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义全局异常处理器
 * @date : 2022-06-25
 * @author : Ice'Clean
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获所有用户自定义异常
     * @param exception 用户自定义异常
     * @return 异常及请求信息
     */
    @ExceptionHandler(value = GlobalException.class)
    public Response handleGlobalException(GlobalException exception){
        return new Response(exception.getStatus())
                .addData("success", false)
                .setMsg(exception.getStatus().msg() + exception.getExtraMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Response handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Response response = new Response(ResponseStatusEnum.REQUEST_PARAM_ILLEGAL);
        List<Pair<String, String>> errList = ex.getBindingResult().getFieldErrors().stream().map(fieldError ->
                new Pair<>(fieldError.getField(), fieldError.getDefaultMessage())).collect(Collectors.toList());
        return response.addData("total", errList.size()).addData("list", errList);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public Response handleConstraintViolationException(ConstraintViolationException ex) {
        Response response = new Response(ResponseStatusEnum.REQUEST_PARAM_ILLEGAL);
        List<Pair<String, String>> errList = ex.getConstraintViolations().stream().map(constraintViolation ->
                new Pair<>(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage()))
                .collect(Collectors.toList());
        return response.addData("total", errList.size()).addData("list", errList);
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
        exception.printStackTrace();
        if (exception instanceof AuthenticationException) {
            String extraMessage = exception.getMessage();
            if (extraMessage.equals("Bad credentials")) {
                extraMessage = "用户名或密码错误";
            }
            // SpringSecurity 的剩余认证异常需要额外捕获
            return new Response().setStatus(ResponseStatusEnum.AUTHENTICATION_ERROR)
                    .setMsg(ResponseStatusEnum.AUTHENTICATION_ERROR.msg() + extraMessage);
        } else if (exception instanceof AccessDeniedException) {
            // SpringSecurity 的剩余权限异常需要额外捕获
            return new Response().setStatus(ResponseStatusEnum.AUTHORITY_ERROR)
                    .setMsg(ResponseStatusEnum.AUTHENTICATION_ERROR.msg() + exception.getMessage());
        }

        //系统未知异常（剩余异常）捕获
        return new Response().setStatus(ResponseStatusEnum.UNKNOWN_ERROR);
    }
}
