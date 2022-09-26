package top.iceclean.chatspace.validation.annotation;

import top.iceclean.chatspace.validation.RequestValidator;
import top.iceclean.chatspace.validation.UserValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE;

/**
 * 表示不是当前用户自己
 * @author : Ice'Clean
 * @date : 2022-08-23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD, PARAMETER, TYPE})
@Constraint(validatedBy = {UserValidator.NotSelfValidator.class, RequestValidator.NotSelfValidator.class})
public @interface NotSelf {
    String message() default "目标对象非法：为用户本身";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
