package com.colak.springtutorial.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class DistributedLock {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean acquire(String lockName, long timeout, TimeUnit unit) {
        String key = "lock:" + lockName;
        return Boolean.TRUE.equals(redisTemplate.opsForValue()
                .setIfAbsent(key, "locked", timeout, unit));
    }

    public boolean release(String lockName) {
        String key = "lock:" + lockName;
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }
}
