/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime;

import static org.mule.util.Preconditions.checkArgument;
import org.mule.extension.runtime.OperationContext;
import org.mule.extension.runtime.OperationExecutor;
import org.mule.extension.runtime.event.OperationFailedEvent;
import org.mule.extension.runtime.event.OperationFailedHandler;
import org.mule.extension.runtime.event.OperationSuccessfulEvent;
import org.mule.extension.runtime.event.OperationSuccessfulHandler;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractOperationExecutor implements OperationExecutor
{

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOperationExecutor.class);
    private EventBus eventBus = new EventBus();

    @Override
    public void onOperationSuccessful(final OperationSuccessfulHandler handler)
    {
        checkArgument(handler != null, "cannot register null handler");
        eventBus.register(new OperationSuccessfulHandler()
        {
            @Subscribe
            @Override
            public void on(OperationSuccessfulEvent event)
            {
                handler.on(event);
            }
        });
    }

    @Override
    public void onOperationFailed(final OperationFailedHandler handler)
    {
        checkArgument(handler != null, "cannot register null handler");
        eventBus.register(new OperationFailedHandler()
        {
            @Subscribe
            @Override
            public void on(OperationFailedEvent event)
            {
                handler.on(event);
            }
        });
    }

    @Override
    public final Object execute(OperationContext operationContext) throws Exception
    {
        Object result;
        try
        {
            result = doExecute(operationContext);
            dispatchOperationSuccessful(operationContext, result);
        }
        catch (Exception e)
        {
            dispatchOperationFailedEvent(operationContext, e);
            throw e;
        }

        return result;
    }

    protected abstract Object doExecute(OperationContext operationContext) throws Exception;

    private void dispatchOperationSuccessful(OperationContext operationContext, Object result)
    {
        eventBus.post(new OperationSuccessfulEvent(operationContext, result));
    }

    private void dispatchOperationFailedEvent(OperationContext operationContext, Exception exception)
    {
        try
        {
            eventBus.post(new OperationFailedEvent(operationContext, exception));
        }
        catch (Exception e)
        {
            LOGGER.error(String.format("Could not dispatch %s event", OperationFailedEvent.class.getName()), e);
        }
    }
}
