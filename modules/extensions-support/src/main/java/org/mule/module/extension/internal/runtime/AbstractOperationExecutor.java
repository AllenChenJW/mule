/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime;

import org.mule.extension.runtime.OperationContext;
import org.mule.extension.runtime.OperationExecutor;

import java.util.function.Consumer;

public abstract class AbstractOperationExecutor implements OperationExecutor
{

    @Override
    public final Object execute(OperationContext operationContext) throws Exception
    {
        Object result;
        try
        {
            result = doExecute(operationContext);
            notify(operationContext, adapter -> adapter.notifySuccessfulOperation(result));
        }
        catch (Exception e)
        {
            notify(operationContext, adapter -> adapter.notifyFailedOperation(e));
            throw e;
        }

        return result;
    }

    protected abstract Object doExecute(OperationContext operationContext) throws Exception;

    private void notify(OperationContext operationContext, Consumer<OperationContextAdapter> notifier)
    {
        if (operationContext instanceof OperationContextAdapter)
        {
            notifier.accept((OperationContextAdapter) operationContext);
        }
    }
}
