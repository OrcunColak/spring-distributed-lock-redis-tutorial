package com.colak.springtutorial.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * distributed lock
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface RedisLock {

    String key() default "your-biz-key";

    long lockExpirationTimeInSeconds() default 60L;

    long lockWaitingTimeInMilliSeconds() default 5000L;

}