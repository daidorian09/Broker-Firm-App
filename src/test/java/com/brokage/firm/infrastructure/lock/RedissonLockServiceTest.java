package com.brokage.firm.infrastructure.lock;

import com.brokage.firm.application.configuration.BrokerApplicationConfig;
import com.brokage.firm.domain.exception.LockAcquisitionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedissonLockServiceTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock rLock;

    @Mock
    private BrokerApplicationConfig brokerApplicationConfig;

    @Mock
    private BrokerApplicationConfig.LockingTimeConfig lockingTimeConfig;

    @InjectMocks
    private RedissonLockService lockService;

    @Test
    void executeWithLock_shouldRunCallable_whenLockIsAcquired() throws Exception {
        when(redissonClient.getLock("test-key")).thenReturn(rLock);
        when(lockingTimeConfig.getWaitTimeInSeconds()).thenReturn(1);
        when(lockingTimeConfig.getLeaseTimeInSeconds()).thenReturn(5);
        when(brokerApplicationConfig.getLockingTime()).thenReturn(lockingTimeConfig);
        when(rLock.tryLock(1, 5, TimeUnit.SECONDS)).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);

        final String result = lockService.executeWithLock("test-key", () -> "locked-success");

        assertThat(result).isEqualTo("locked-success");
        verify(rLock).unlock();
    }

    @Test
    void executeWithLock_shouldThrow_whenLockNotAcquired() throws Exception {
        // Arrange
        when(redissonClient.getLock("test-key")).thenReturn(rLock);
        when(lockingTimeConfig.getWaitTimeInSeconds()).thenReturn(1);
        when(lockingTimeConfig.getLeaseTimeInSeconds()).thenReturn(5);
        when(brokerApplicationConfig.getLockingTime()).thenReturn(lockingTimeConfig);
        when(rLock.tryLock(1, 5, TimeUnit.SECONDS)).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() ->
                lockService.executeWithLock("test-key", () -> {
                    throw new IllegalStateException("should not execute");
                }))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Exception during locked execution");

        verify(rLock, never()).unlock();
    }


    @Test
    void executeWithLockRunnable_shouldExecuteRunnable() {
        when(redissonClient.getLock("run-key")).thenReturn(rLock);
        when(lockingTimeConfig.getWaitTimeInSeconds()).thenReturn(1);
        when(lockingTimeConfig.getLeaseTimeInSeconds()).thenReturn(5);
        when(brokerApplicationConfig.getLockingTime()).thenReturn(lockingTimeConfig);
        try {
            when(rLock.tryLock(1, 5, TimeUnit.SECONDS)).thenReturn(true);
        } catch (InterruptedException e) {
            fail("Unexpected InterruptedException");
        }
        when(rLock.isHeldByCurrentThread()).thenReturn(true);

        final Runnable runnable = mock(Runnable.class);

        lockService.executeWithLock("run-key", runnable);

        verify(runnable).run();
        verify(rLock).unlock();
    }

    @Test
    void executeWithLock_shouldThrow_whenInterruptedDuringLockAcquisition() throws Exception {
        when(redissonClient.getLock("interrupt-key")).thenReturn(rLock);
        when(lockingTimeConfig.getWaitTimeInSeconds()).thenReturn(1);
        when(lockingTimeConfig.getLeaseTimeInSeconds()).thenReturn(5);
        when(brokerApplicationConfig.getLockingTime()).thenReturn(lockingTimeConfig);

        when(rLock.tryLock(1, 5, TimeUnit.SECONDS)).thenThrow(new InterruptedException("Simulated interrupt"));

        assertThatThrownBy(() ->
                lockService.executeWithLock("interrupt-key", () -> "should-not-run"))
                .isInstanceOf(LockAcquisitionException.class)
                .hasMessageContaining("interrupted");

        assertThat(Thread.currentThread().isInterrupted()).isTrue();
    }

    @Test
    void shouldNotUnlock_whenLockNotHeldByCurrentThread() throws Exception {
        when(redissonClient.getLock("foreign-lock")).thenReturn(rLock);
        when(lockingTimeConfig.getWaitTimeInSeconds()).thenReturn(1);
        when(lockingTimeConfig.getLeaseTimeInSeconds()).thenReturn(10);
        when(brokerApplicationConfig.getLockingTime()).thenReturn(lockingTimeConfig);
        when(rLock.tryLock(1, 10, TimeUnit.SECONDS)).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(false);

        String result = lockService.executeWithLock("foreign-lock", () -> "skip-unlock");

        assertThat(result).isEqualTo("skip-unlock");
        verify(rLock, never()).unlock();
    }
}