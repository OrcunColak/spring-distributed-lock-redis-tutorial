package com.colak.springtutorial.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DistributedLockTest {

    @Autowired
    private DistributedLock distributedLock;

    @Test
    void acquire() {
        String lockName = "my-lock";
        String lockValue = "my-lock-value";

        boolean acquire = distributedLock.acquire(lockName, lockValue, 1, TimeUnit.MINUTES);
        assertTrue(acquire);

        boolean release = distributedLock.release(lockName, lockValue);
        assertTrue(release);
    }
}