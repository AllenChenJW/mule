/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime.config;

import org.mule.extension.runtime.ExpirationPolicy;

import java.util.concurrent.TimeUnit;

public class NullExpirationPolicy implements ExpirationPolicy
{

    public static final ExpirationPolicy INSTANCE = new NullExpirationPolicy();

    private NullExpirationPolicy()
    {
    }

    @Override
    public boolean isExpired(long lastUsed, TimeUnit timeUnit)
    {
        return false;
    }

    @Override
    public long getMaxIdleTime()
    {
        return 0;
    }

    @Override
    public TimeUnit getTimeUnit()
    {
        return TimeUnit.MILLISECONDS;
    }
}
