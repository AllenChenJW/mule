/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime.config;

import java.util.concurrent.atomic.AtomicInteger;

public class DynamicConfigurationInstanceHolder
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

    public boolean isInUse()
    {
        return usageCount.get() > 0;
    }

    public long getLastUsedMillis()
    {
        return lastUsedMillis;
    }

    public String getRegistrationName()
    {
        return registrationName;
    }

    private long now()
    {
        return System.currentTimeMillis();
    }
}
