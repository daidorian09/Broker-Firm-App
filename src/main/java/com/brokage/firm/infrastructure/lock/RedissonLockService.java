package com.brokage.firm.infrastructure.lock;

import com.brokage.firm.application.configuration.BrokerApplicationConfig;
import com.brokage.firm.application.service.LockService;
import com.brokage.firm.domain.exception.LockAcquisitionException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedissonLockService implements LockService {

    private final RedissonClient redissonClient;
    private final BrokerApplicationConfig brokerApplicationConfig;

    @Override
    public <T> T executeWithLock(final String key, final Callable<T> action) {
        final RLock lock = redissonClient.getLock(key);
        try {
            final boolean acquired = lock.tryLock(brokerApplicationConfig.getLockingTime().getWaitTimeInSeconds(),
                    brokerApplicationConfig.getLockingTime().getLeaseTimeInSeconds(),
                    TimeUnit.SECONDS);
            if (!acquired) {
                throw new LockAcquisitionException("Could not acquire lock for key: " + key);
            }
            return action.call();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockAcquisitionException("Lock acquisition interrupted for key: " + key);
        } catch (Exception e) {
            throw new RuntimeException("Exception during locked execution", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public void executeWithLock(final String key, final Runnable action) {
        executeWithLock(key, Executors.callable(action));
    }
}