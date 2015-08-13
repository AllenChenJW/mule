/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime.config;

import org.mule.extension.runtime.ExpirationPolicy;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class DefaultExpirationPolicy implements ExpirationPolicy
{
    private final long maxIdleTime;
    private final TimeUnit timeUnit;
    private final Supplier<Long> timeSupplier;

    public DefaultExpirationPolicy(long maxIdleTime, TimeUnit timeUnit, Supplier<Long> timeSupplier)
    {
        this.maxIdleTime = maxIdleTime;
        this.timeUnit = timeUnit;
        this.timeSupplier = timeSupplier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExpired(long lastUsed, TimeUnit timeUnit)
    {
        long idleTimeMillis = timeSupplier.get() - timeUnit.toMillis(lastUsed);
        return idleTimeMillis > this.timeUnit.toMillis(maxIdleTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getMaxIdleTime()
    {
        return maxIdleTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimeUnit getTimeUnit()
    {
        return timeUnit;
    }
}
