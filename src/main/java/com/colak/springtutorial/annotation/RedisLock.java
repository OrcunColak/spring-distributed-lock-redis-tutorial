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

    long DEFAULT_TIMEOUT_FOR_LOCK = 5L;
    long DEFAULT_EXPIRE_TIME = 60L;

    String key() default "your-biz-key";

    long expiredTime() default DEFAULT_EXPIRE_TIME;

    long timeoutForLock() default DEFAULT_TIMEOUT_FOR_LOCK;

}