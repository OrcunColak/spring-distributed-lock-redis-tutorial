package com.colak.springtutorial.annotation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class DistributedLock {

    private final RedisTemplate<String, String> redisTemplate;

    // Redis key prefix for the lock
    private static final String DEFAULT_KEY_PREFIX = "lock:";

    public boolean acquire(String key, String lockValue, long timeout, TimeUnit unit) {
        String lockKey = getLockKey(key);
        Boolean isLocked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, timeout, unit);
        return Boolean.TRUE.equals(isLocked);
    }

    public boolean acquireLockWaitUntil(String key, String lockValue, long lockWaitingTimeInMilliSeconds, long timeout, TimeUnit unit) {
        String lockKey = getLockKey(key);

        long deadline = System.currentTimeMillis() + lockWaitingTimeInMilliSeconds;

        boolean acquired = false;
        while (System.currentTimeMillis() < deadline) {
            Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, timeout, unit);
            if (Boolean.TRUE.equals(isLocked)) {
                acquired = true;
                break;
            }

            // Sleep for a short duration to prevent aggressive CPU usage in the spin loop
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        // Return whether the lock was successfully acquired
        return acquired;
    }

    public boolean release(String key, String lockValue) {
        String lockKey = getLockKey(key);
        String currentValue = redisTemplate.opsForValue().get(lockKey);

        boolean isReleased = false;

        // See https://medium.com/@anil.goyal0057/distributed-locking-mechanism-using-redis-26c17d9f3d5f
        // This is important to avoid removing a lock that was created by another client.
        // For example, a client may acquire the lock, get blocked performing some operation for longer than the lock
        // validity time (the time at which the key will expire), and later remove the lock, that was already acquired
        // by some other client.
        if (currentValue != null && currentValue.equals(lockValue)) {
            isReleased = Boolean.TRUE.equals(redisTemplate.delete(lockKey));
        }
        return isReleased;
    }

    private static String getLockKey(String lockValue) {
        return DEFAULT_KEY_PREFIX + lockValue;
    }
}
