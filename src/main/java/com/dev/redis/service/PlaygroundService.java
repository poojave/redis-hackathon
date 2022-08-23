package com.dev.redis.service;

import com.dev.redis.service.locker.DistributedLocker;
import com.dev.redis.service.locker.LockExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;

/**
 * Service used to test out our other implementations.
 *
 * @author mirza.
 */
@Service
public class PlaygroundService {
    private static final Logger LOG = LoggerFactory.getLogger(PlaygroundService.class);

    private final DistributedLocker locker;

    @Autowired
    public PlaygroundService(DistributedLocker locker) {
        this.locker = locker;
    }

    @PostConstruct
    private void setup() {
        for (int i=0; i<10000; i++) { // changed this number to run this experiment
            int finalI = i;
            CompletableFuture.runAsync(() -> runTask(String.valueOf(finalI), 60000));
        }
    }

    private void runTask(final String taskNumber, final long sleep) {
        LOG.info("Running task : '{}'", taskNumber);

        LockExecutionResult<String> result = locker.lock("Task"+taskNumber, 62, 25, () -> {
            LOG.info("Sleeping for '{}' ms", sleep);
            Thread.sleep(sleep);
            LOG.info("Executing task '{}'", taskNumber);
            return taskNumber;
        });

        LOG.info("Task result : '{}' -> exception : '{}'", result.getResultIfLockAcquired(), result.hasException());
    }
}
