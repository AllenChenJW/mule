/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.manager;

import static org.mule.util.MapUtils.idempotentPut;
import org.mule.extension.runtime.ConfigurationInstanceProvider;
import org.mule.extension.runtime.ExpirableContainer;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class ConfigurationInstanceProviderWrapper implements ExpirableContainer<Object>
{

    private final ConfigurationInstanceProvider<?> configurationInstanceProvider;
    private final Map<String, Object> configurationInstances = new ConcurrentHashMap<>();

    ConfigurationInstanceProviderWrapper(ConfigurationInstanceProvider<?> configurationInstanceProvider)
    {
        this.configurationInstanceProvider = configurationInstanceProvider;
    }

    ConfigurationInstanceProvider<?> getConfigurationInstanceProvider()
    {
        return configurationInstanceProvider;
    }

    void addConfigurationInstance(String registrationName, Object configurationInstance)
    {
        idempotentPut(configurationInstances, registrationName, configurationInstance);
    }

    @Override
    public Map<String, Object> getExpired()
    {
        if (configurationInstanceProvider instanceof ExpirableContainer)
        {
            return ImmutableMap.copyOf(((ExpirableContainer<Object>) configurationInstanceProvider).getExpired());
        }

        return ImmutableMap.of();
    }
}
