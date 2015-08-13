/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime.config;

import org.mule.extension.runtime.Expirable;
import org.mule.extension.runtime.ExpirationPolicy;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicConfigurationInstanceHolder implements Expirable
{

    private final String registrationName;
    private final Object configurationInstance;

    private final AtomicInteger usageCount = new AtomicInteger(0);
    private long lastUsedMillis = now();

    public DynamicConfigurationInstanceHolder(String registrationName, Object configurationInstance)
    {
        this.registrationName = registrationName;
        this.configurationInstance = configurationInstance;
    }

    @Override
    public boolean isExpired(ExpirationPolicy expirationPolicy)
    {
        return isNotInUse() && expirationPolicy.isExpired(lastUsedMillis, TimeUnit.MILLISECONDS);
    }

    public Object getConfigurationInstance()
    {
        return configurationInstance;
    }

    public int accountUsage()
    {
        return usageCount.incrementAndGet();
    }

    public int discountUsage()
    {
        lastUsedMillis = now();
        return usageCount.decrementAndGet();
    }

    public String getRegistrationName()
    {
        return registrationName;
    }

    private boolean isNotInUse()
    {
        return usageCount.get() < 1;
    }

    private long now()
    {
        return System.currentTimeMillis();
    }
}
