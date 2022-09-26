package top.iceclean.chatspace.validation;

import org.springframework.security.core.context.SecurityContextHolder;
import top.iceclean.chatspace.po.User;
import top.iceclean.chatspace.pojo.UserAuthority;
import top.iceclean.chatspace.validation.annotation.NotSelf;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.util.function.Predicate;

/**
 * @author : Ice'Clean
 * @date : 2022-08-23
 */
public class UserValidator<T extends Annotation> implements ConstraintValidator<T, User> {

    protected Predicate<User> predicate = user -> false;

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        return predicate.test(user);
    }

    /** 基于用户对象的非用户本身认证器 */
    public static class NotSelfValidator extends UserValidator<NotSelf> {
        @Override
        public void initialize(NotSelf constraintAnnotation) {
            predicate = user -> {
                // 拿到用户上下文
                UserAuthority authority = (UserAuthority) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                return !user.getUserId().equals(authority.getUser().getUserId());
            };
        }
    }
}
