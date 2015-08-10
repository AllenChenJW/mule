/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime;

import static org.mule.module.extension.internal.util.MuleExtensionUtils.asOperationContextAdapter;
import org.mule.api.MuleEvent;
import org.mule.api.MuleRuntimeException;
import org.mule.extension.runtime.ConfigurationInstanceProvider;
import org.mule.extension.runtime.OperationContext;
import org.mule.extension.runtime.event.OperationFailedEvent;
import org.mule.extension.runtime.event.OperationFailedHandler;
import org.mule.extension.runtime.event.OperationSuccessfulEvent;
import org.mule.extension.runtime.event.OperationSuccessfulHandler;
import org.mule.module.extension.internal.runtime.resolver.ResolverSet;
import org.mule.module.extension.internal.runtime.resolver.ResolverSetResult;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * A {@link ConfigurationInstanceProvider} which continuously evaluates the same
 * {@link ResolverSet} and then uses the resulting {@link ResolverSetResult}
 * to build an instance of a given type.
 * <p/>
 * Although each invocation to {@link #get(OperationContext)} is guaranteed to end up in an invocation
 * to {@link #resolverSet#resolve(MuleEvent)}, the resulting {@link ResolverSetResult} might not end up
 * generating a new instance. This is so because {@link ResolverSetResult} instances are put in a cache to
 * guarantee that equivalent evaluations of the {@code resolverSet} return the same instance.
 *
 * @since 3.7.0
 */
public final class DynamicConfigurationInstanceProvider<T> implements ConfigurationInstanceProvider<T>
{

    private final ConfigurationObjectBuilder configurationObjectBuilder;
    private final ResolverSet resolverSet;
    private final LoadingCache<ResolverSetResult, DynamicConfigurationInstanceHolder> cache =
            CacheBuilder.newBuilder().build(new CacheLoader<ResolverSetResult, DynamicConfigurationInstanceHolder>()
            {
                @Override
                public DynamicConfigurationInstanceHolder load(ResolverSetResult resolverSetResult) throws Exception
                {
                    Object configurationInstance = configurationObjectBuilder.build(resolverSetResult);
                    return new DynamicConfigurationInstanceHolder(configurationInstance);
                }
            });

    /**
     * Creates a new instance
     *
     * @param configurationObjectBuilder the {@link ConfigurationObjectBuilder} that will build the configuration instances
     * @param resolverSet                the {@link ResolverSet} that's going to be evaluated
     */
    public DynamicConfigurationInstanceProvider(ConfigurationObjectBuilder configurationObjectBuilder,
                                                ResolverSet resolverSet)
    {
        this.configurationObjectBuilder = configurationObjectBuilder;
        this.resolverSet = resolverSet;
    }

    /**
     * Evaluates {@link #resolverSet} using the given {@code event} and returns
     * an instance produced with the result. For equivalent {@link ResolverSetResult}s
     * it will return the same instance.
     *
     * @param operationContext a {@link OperationContext}
     * @return the resolved value
     */
    @Override
    public T get(OperationContext operationContext)
    {
        try
        {
            ResolverSetResult result = resolverSet.resolve(asOperationContextAdapter(operationContext).getEvent());
            DynamicConfigurationInstanceHolder configurationInstanceHolder = cache.getUnchecked(result);
            OperationCompletedHandler handler = new OperationCompletedHandler(configurationInstanceHolder);

            operationContext.getOperationExecutor().onOperationSuccessful(handler);
            operationContext.getOperationExecutor().onOperationFailed(handler);

            configurationInstanceHolder.addInflightUsage();
            return (T) configurationInstanceHolder.getConfigurationInstance();
        }
        catch (Exception e)
        {
            throw new MuleRuntimeException(e);
        }
    }

    private class OperationCompletedHandler implements OperationSuccessfulHandler, OperationFailedHandler {
        private final DynamicConfigurationInstanceHolder configurationInstanceHolder;

        private OperationCompletedHandler(DynamicConfigurationInstanceHolder configurationInstanceHolder)
        {
            this.configurationInstanceHolder = configurationInstanceHolder;
        }

        @Override
        public void on(OperationFailedEvent event)
        {
            discount();
        }

        @Override
        public void on(OperationSuccessfulEvent event)
        {
            discount();
        }

        private void discount() {
            configurationInstanceHolder.discountInflightUsage();
        }
    }
}
