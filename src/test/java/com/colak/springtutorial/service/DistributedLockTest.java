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
        boolean acquire = distributedLock.acquire(lockName, 1, TimeUnit.MINUTES);
        assertTrue(acquire);

        boolean release = distributedLock.release(lockName);
        assertTrue(release);
    }
}