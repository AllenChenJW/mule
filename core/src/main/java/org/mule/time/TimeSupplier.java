/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.time;

import java.util.function.Supplier;

public class TimeSupplier implements Supplier<Long>
{

    @Override
    public Long get()
    {
        return System.currentTimeMillis();
    }
}
