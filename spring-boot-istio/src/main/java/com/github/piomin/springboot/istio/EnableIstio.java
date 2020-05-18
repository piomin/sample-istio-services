package com.github.piomin.springboot.istio;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableIstio {
    int timeout() default 0;
    String version() default "";
    int weight() default 0;
    int numberOfRetries() default 0;
}
