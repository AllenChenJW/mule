/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime.config;

import java.util.concurrent.TimeUnit;

public class DefaultExpirationPolicy
{
    private final long maxIdleTime;
    private final TimeUnit timeUnit;

    public DefaultExpirationPolicy(long maxIdleTime, TimeUnit timeUnit)
    {
        this.maxIdleTime = maxIdleTime;
        this.timeUnit = timeUnit;
    }

    public boolean shouldExpirePerIdleTime(long idleTime, TimeUnit idleTimeUnit) {
        long idleTimeMillis = System.currentTimeMillis() - idleTimeUnit.toMillis(idleTime);
        return timeUnit.toMillis(maxIdleTime) > idleTimeMillis;
    }


    public long getMaxIdleTime()
    {
        return maxIdleTime;
    }

    public TimeUnit getTimeUnit()
    {
        return timeUnit;
    }
}
