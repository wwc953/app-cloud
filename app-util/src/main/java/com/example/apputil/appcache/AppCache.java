package com.example.apputil.appcache;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AppCache {
    String key() default "defaultKey";

    String value() default "defaultValue";

    int expire() default -1;
}
