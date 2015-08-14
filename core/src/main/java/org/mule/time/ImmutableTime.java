/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.time;

import java.util.concurrent.TimeUnit;

public class ImmutableTime implements Time
{
    private final long time;
    private final TimeUnit timeUnit;

    public ImmutableTime(long time, TimeUnit timeUnit)
    {
        this.time = time;
        this.timeUnit = timeUnit;
    }

    @Override
    public long getTime()
    {
        return time;
    }

    @Override
    public TimeUnit getUnit()
    {
        return timeUnit;
    }
}
