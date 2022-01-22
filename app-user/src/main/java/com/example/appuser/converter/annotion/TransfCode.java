package com.example.appuser.converter.annotion;

import java.lang.annotation.*;

/**
 * 配置属性上，对code进行翻译
 */
@Inherited
//@JsonSerialize
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
//@JacksonAnnotationsInside
public @interface TransfCode {
    /**
     * 值来源
     *
     * @return
     */
    String valueFrom() default "";

    /**
     * 字典码
     *
     * @return
     */
    String codeType() default "";

    public static String Mgt_ORG_CODE = "mgtOrgCode";
}
