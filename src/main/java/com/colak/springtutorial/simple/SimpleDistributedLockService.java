package com.colak.springtutorial.simple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

// See https://gurselgazii.medium.com/implementing-distributed-locks-in-spring-boot-with-redisson-2967149bcb7c

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleDistributedLockService {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean acquireLock(String lockKey, String value, long timeout, TimeUnit unit) {
        Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, value, timeout, unit);
        return Boolean.TRUE.equals(isLocked);
    }

    public void releaseLock(String lockKey) {
        redisTemplate.delete(lockKey);
    }

}
