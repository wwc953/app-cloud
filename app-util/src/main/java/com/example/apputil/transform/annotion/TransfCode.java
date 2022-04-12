package com.example.apputil.transform.annotion;

import com.example.apputil.transform.serialize.TransformSerialize;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.*;

@Inherited
//@JsonSerialize(using = TransformSerialize.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
//@JacksonAnnotationsInside
public @interface TransfCode {

    /**
     * 来源
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
}
