package top.iceclean.chatspace.validation;

import org.springframework.security.core.context.SecurityContextHolder;
import top.iceclean.chatspace.DTO.RequestDTO;
import top.iceclean.chatspace.constant.SessionType;
import top.iceclean.chatspace.po.SessionRequest;
import top.iceclean.chatspace.pojo.UserAuthority;
import top.iceclean.chatspace.validation.annotation.NotSelf;
import top.iceclean.chatspace.validation.annotation.ValidRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.util.function.Predicate;

/**
 * 好友请求和群聊请求的验证
 * ① 目标好友不是用户本身
 * ① 已经是好友或已经加入群聊的，不能再添加
 * ② 非好友或没有进入群聊的，不能进行删除
 * @author : Ice'Clean
 * @date : 2022-08-22
 */
public class RequestValidator<T extends Annotation> implements ConstraintValidator<T, RequestDTO> {

    protected Predicate<RequestDTO> predicate = request -> true;

    @Override
    public boolean isValid(RequestDTO request, ConstraintValidatorContext context) {
        return predicate.test(request);
    }

    /** 基于申请对象的非用户本身认证器 */
    public static class NotSelfValidator extends RequestValidator<NotSelf> {
        @Override
        public void initialize(NotSelf constraintAnnotation) {
            predicate = request -> {
                // 拿到用户上下文
                UserAuthority authority = (UserAuthority) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                // 当为群聊时，直接返回真，否则判断请求的 ID 不能等于当前用户 ID
                return request.getType() == SessionType.GROUP || !authority.getUser().getUserId().equals(request.getTargetId());
            };
        }
    }
}