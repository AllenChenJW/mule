/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.manager;

import static org.mule.config.i18n.MessageFactory.createStaticMessage;
import static org.mule.util.Preconditions.checkArgument;
import static org.mule.util.concurrent.ThreadNameHelper.getPrefix;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.MuleRuntimeException;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultConfigurationExpirationManager implements Startable, Stoppable
{

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfigurationExpirationManager.class);

    public static class Builder
    {

        public static Builder newBuilder(ExtensionRegistry extensionRegistry, MuleContext muleContext)
        {
            Builder builder = new Builder();
            builder.manager.extensionRegistry = extensionRegistry;
            builder.manager.muleContext = muleContext;

            return builder;
        }

        private DefaultConfigurationExpirationManager manager = new DefaultConfigurationExpirationManager();

        private Builder()
        {
        }

        public Builder runEvery(long frequency, TimeUnit timeUnit)
        {
            manager.frequency = frequency;
            manager.timeUnit = timeUnit;

            return this;
        }

        public Builder onExpired(BiConsumer<String, Object> expirationHandler)
        {
            manager.expirationHandler = expirationHandler;
            return this;
        }

        public DefaultConfigurationExpirationManager build()
        {
            checkArgument(manager.extensionRegistry != null, "extensionRegistry cannot be null");
            checkArgument(manager.muleContext != null, "muleContext cannot be null");
            checkArgument(manager.frequency > 0, "frequency must be greater than zero");
            checkArgument(manager.timeUnit != null, "timeUnit cannot be null");

            return manager;
        }

    }

    private ExtensionRegistry extensionRegistry;
    private MuleContext muleContext;
    private long frequency;
    private TimeUnit timeUnit;
    private BiConsumer<String, Object> expirationHandler;

    private ScheduledExecutorService executor;

    private DefaultConfigurationExpirationManager()
    {
    }

    @Override
    public void start() throws MuleException
    {
        executor = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, getPrefix(muleContext) + "extension.expiration.manager"));
        executor.scheduleWithFixedDelay(() -> expire(), frequency, frequency, timeUnit);
    }

    private void expire()
    {
        LOGGER.debug("Running configuration expiration cycle");
        try
        {
            Map<String, Object> expired = extensionRegistry.getExpiredConfigInstances();
            if (LOGGER.isDebugEnabled())
            {
                if (expired.isEmpty())
                {
                    LOGGER.debug("No configurations elegible for expiration were found");
                }
                else
                {
                    LOGGER.debug("Found {} expirable configurations", expired.size());
                }
            }

            expired.entrySet().stream().forEach(this::handleExpiration);
        }
        catch (Exception e)
        {
            LOGGER.error("Found exception trying to expire idle configurations. Will try again on next cycle", e);
        }

    }

    private void handleExpiration(Map.Entry<String, Object> config)
    {
        final String key = config.getKey();
        LOGGER.debug("Expiring configuration of key {}", key);
        try
        {
            expirationHandler.accept(key, config.getValue());
            LOGGER.debug("Configuration of key {} was expired", key);
        }
        catch (Exception e)
        {
            LOGGER.error(String.format("Could not process expiration for dynamic config '%s' of type '%s'. Will try again on next cycle",
                                       key, config.getValue().getClass().getName()), e);
        }
    }

    @Override
    public void stop() throws MuleException
    {
        executor.shutdown();
        try
        {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            throw new MuleRuntimeException(createStaticMessage("Exception found while waiting for expiration thread to finish"), e);
        }
    }
}
