/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class TestTimeSupplier implements Supplier<Long>
{

    private long time;

    public TestTimeSupplier(long time)
    {
        this.time = time;
    }

    @Override
    public Long get()
    {
        return time;
    }

    public long move(long time, TimeUnit unit)
    {
        return this.time += unit.toMillis(time);
    }
}
