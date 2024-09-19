package com.colak.springtutorial.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class DistributedLock {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean acquire(String lockValue, long timeout, TimeUnit unit) {
        String lockKey = getLockKey(lockValue);
        return redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "locked", timeout, unit);
    }

    public boolean release(String lockValue) {
        String lockKey = getLockKey(lockValue);
        String currentValue = redisTemplate.opsForValue().get(lockKey);

        // See https://medium.com/@anil.goyal0057/distributed-locking-mechanism-using-redis-26c17d9f3d5f
        // This is important to avoid removing a lock that was created by another client.
        // For example, a client may acquire the lock, get blocked performing some operation for longer than the lock
        // validity time (the time at which the key will expire), and later remove the lock, that was already acquired
        // by some other client.
        if (currentValue != null && currentValue.equals(lockValue)) {
            return redisTemplate.delete(lockKey);
        }
        return false;
    }

    private static String getLockKey(String lockValue) {
        return "lock:" + lockValue;
    }
}
