package com.example.appuser.converter.annotion;

import java.lang.annotation.*;

/**
 * 对user进行翻译, userid --> userName
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TransfUser {
    String valueFrom() default "";
}
