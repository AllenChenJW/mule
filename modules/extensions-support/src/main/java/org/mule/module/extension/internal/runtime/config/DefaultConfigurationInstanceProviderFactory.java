/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime.config;

import static org.mule.module.extension.internal.util.MuleExtensionUtils.getInitialiserEvent;
import org.mule.api.MuleContext;
import org.mule.extension.ExtensionManager;
import org.mule.extension.introspection.Configuration;
import org.mule.extension.introspection.Extension;
import org.mule.extension.runtime.ConfigurationInstanceProvider;
import org.mule.extension.runtime.ExpirationPolicy;
import org.mule.module.extension.internal.manager.ExtensionManagerAdapter;
import org.mule.module.extension.internal.runtime.resolver.ResolverSet;

public final class DefaultConfigurationInstanceProviderFactory implements ConfigurationInstanceProviderFactory
{

    @Override
    public <T> ConfigurationInstanceProvider<T> createConfigurationInstanceProvider(
            String name,
            Extension extension,
            Configuration configuration,
            ResolverSet resolverSet,
            MuleContext muleContext,
            ExtensionManagerAdapter extensionManager,
            ExpirationPolicy expirationPolicy) throws Exception
    {
        ConfigurationObjectBuilder configurationObjectBuilder = new ConfigurationObjectBuilder(configuration, resolverSet);
        ConfigurationInstanceProvider<T> configurationInstanceProvider;

        if (resolverSet.isDynamic())
        {
            configurationInstanceProvider = new DynamicConfigurationInstanceProvider<>(name,
                                                                                       extension,
                                                                                       extensionManager,
                                                                                       configurationObjectBuilder,
                                                                                       resolverSet,
                                                                                       expirationPolicy);

            register(extension, name, configurationInstanceProvider, extensionManager);
        }
        else
        {
            T configurationInstance = (T) configurationObjectBuilder.build(getInitialiserEvent(muleContext));
            configurationInstanceProvider = new StaticConfigurationInstanceProvider<>(configurationInstance);
            register(extension, name, configurationInstanceProvider, extensionManager);
            extensionManager.registerConfigurationInstance(extension, name, configurationInstance);
        }

        return configurationInstanceProvider;
    }

    private <T> void register(Extension extension, String name, ConfigurationInstanceProvider<T> configurationInstanceProvider, ExtensionManager extensionManager)
    {
        extensionManager.registerConfigurationInstanceProvider(extension, name, configurationInstanceProvider);
    }
}
