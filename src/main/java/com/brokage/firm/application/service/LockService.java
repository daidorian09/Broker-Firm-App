package com.brokage.firm.application.service;

import java.util.concurrent.Callable;

public interface LockService {
    <T> T executeWithLock(final String key, final Callable<T> action);

    void executeWithLock(final String key, final Runnable action);
}
