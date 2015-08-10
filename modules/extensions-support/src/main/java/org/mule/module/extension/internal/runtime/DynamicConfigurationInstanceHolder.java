/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime;

import java.util.concurrent.atomic.AtomicInteger;

public class DynamicConfigurationInstanceHolder
{
    private final AtomicInteger inflightUsages = new AtomicInteger(0);
    private final Object configurationInstance;

    public DynamicConfigurationInstanceHolder(Object configurationInstance)
    {
        this.configurationInstance = configurationInstance;
    }

    public Object getConfigurationInstance()
    {
        return configurationInstance;
    }

    public int getInfightUsages() {
        return inflightUsages.get();
    }

    public int addInflightUsage() {
        return inflightUsages.incrementAndGet();
    }

    public int discountInflightUsage() {
        return inflightUsages.decrementAndGet();
    }
}
