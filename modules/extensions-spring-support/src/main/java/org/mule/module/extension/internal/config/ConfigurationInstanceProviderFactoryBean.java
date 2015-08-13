/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.config;

import static org.mule.module.extension.internal.config.XmlExtensionParserUtils.getResolverSet;
import org.mule.api.MuleContext;
import org.mule.extension.introspection.Configuration;
import org.mule.extension.introspection.Extension;
import org.mule.extension.runtime.ConfigurationInstanceProvider;
import org.mule.extension.runtime.ExpirationPolicy;
import org.mule.module.extension.internal.manager.ExtensionManagerAdapter;
import org.mule.module.extension.internal.runtime.config.ConfigurationInstanceProviderFactory;
import org.mule.module.extension.internal.runtime.config.DefaultConfigurationInstanceProviderFactory;
import org.mule.module.extension.internal.runtime.config.NullExpirationPolicy;
import org.mule.module.extension.internal.runtime.resolver.ResolverSet;

import org.springframework.beans.factory.FactoryBean;

/**
 * A {@link FactoryBean} which returns a {@link ConfigurationInstanceProvider} that provides the actual instances
 * that implement a given {@link Configuration}. Subsequent invokations to {@link #getObject()} method
 * returns always the same {@link ConfigurationInstanceProvider}.
 *
 * @since 3.7.0
 */
final class ConfigurationInstanceProviderFactoryBean implements FactoryBean<ConfigurationInstanceProvider<Object>>
{

    private final ConfigurationInstanceProvider<Object> configurationInstanceProvider;
    private final ConfigurationInstanceProviderFactory configurationInstanceProviderFactory = new DefaultConfigurationInstanceProviderFactory();
    private ExpirationPolicy expirationPolicy = null;

    ConfigurationInstanceProviderFactoryBean(String name,
                                             Extension extension,
                                             Configuration configuration,
                                             ElementDescriptor element,
                                             MuleContext muleContext)
    {
        final ExtensionManagerAdapter extensionManager = (ExtensionManagerAdapter) muleContext.getExtensionManager();

        ResolverSet resolverSet = getResolverSet(element, configuration.getParameters());
        try
        {
            configurationInstanceProvider = configurationInstanceProviderFactory.createConfigurationInstanceProvider(
                    name,
                    extension,
                    configuration,
                    resolverSet,
                    muleContext,
                    extensionManager,
                    getExpirationPolicy());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ConfigurationInstanceProvider<Object> getObject() throws Exception
    {
        return configurationInstanceProvider;
    }

    /**
     * @return {@link ConfigurationInstanceProvider}
     */
    @Override
    public Class<ConfigurationInstanceProvider> getObjectType()
    {
        return ConfigurationInstanceProvider.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

    private ExpirationPolicy getExpirationPolicy()
    {
        return expirationPolicy != null ? expirationPolicy : NullExpirationPolicy.INSTANCE;
    }

    public void setExpirationPolicy(ExpirationPolicy expirationPolicy)
    {
        this.expirationPolicy = expirationPolicy;
    }
}
