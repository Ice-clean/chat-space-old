package top.iceclean.chatspace.validation.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE;

/**
 * @author : Ice'Clean
 * @date : 2022-08-22
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD, PARAMETER, TYPE})
public @interface ValidRequest {
    String message() default "非法申请，对方已是好友或已经申请过";
}
