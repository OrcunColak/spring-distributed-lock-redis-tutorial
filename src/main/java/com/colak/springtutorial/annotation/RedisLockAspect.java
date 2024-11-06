package com.colak.springtutorial.annotation;

import com.colak.springtutorial.annotation.service.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class RedisLockAspect {

    private final DistributedLock distributedLock;

    // Pointcut to match all methods annotated with @RedisLock
    @Pointcut("@annotation(com.colak.springtutorial.annotation.RedisLock)")
    public void lockAnno() {
    }

    @Around("lockAnno()")
    public void invoke(ProceedingJoinPoint joinPoint) {
        // Get the RedisLock annotation on the intercepted method
        RedisLock annotation = getLockAnnotationOnMethod(joinPoint);

        String key = annotation.key();
        long lockExpirationTimeInSeconds = annotation.lockExpirationTimeInSeconds();
        long lockWaitingTimeInMilliSeconds = annotation.lockWaitingTimeInMilliSeconds();

        String uuid = UUID.randomUUID().toString();

        boolean acquired = distributedLock.acquireLockWaitUntil(key, uuid, lockWaitingTimeInMilliSeconds, lockExpirationTimeInSeconds, TimeUnit.SECONDS);

        // If the lock is not acquired within the timeout, throw an exception, this can be customized based on business needs
        if (!acquired) {
            throw new RuntimeException("Failed to acquire lock");
        }
        try {
            // Execute the business logic
            joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("Error occurred during business execution!");
        } finally {
            distributedLock.release(key, uuid);
        }
    }

    private RedisLock getLockAnnotationOnMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(RedisLock.class);
    }

}
