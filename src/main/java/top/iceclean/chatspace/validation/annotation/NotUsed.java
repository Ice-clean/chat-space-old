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
 * 表示用户名未被占用
 * @author : Ice'Clean
 * @date : 2022-09-26
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD, PARAMETER, TYPE})
@Constraint(validatedBy = {UserValidator.NotUsedValidator.class})
public @interface NotUsed {
    String message() default "目标对象非法：为用户本身";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
