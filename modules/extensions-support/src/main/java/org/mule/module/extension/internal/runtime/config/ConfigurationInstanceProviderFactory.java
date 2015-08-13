package org.mule.module.extension.internal.runtime.config;

import org.mule.api.MuleContext;
import org.mule.extension.introspection.Configuration;
import org.mule.extension.introspection.Extension;
import org.mule.extension.runtime.ConfigurationInstanceProvider;
import org.mule.extension.runtime.ExpirationPolicy;
import org.mule.module.extension.internal.manager.ExtensionManagerAdapter;
import org.mule.module.extension.internal.runtime.resolver.ResolverSet;

public interface ConfigurationInstanceProviderFactory
{

    <T> ConfigurationInstanceProvider<T> createConfigurationInstanceProvider(
            String name,
            Extension extension,
            Configuration configuration,
            ResolverSet resolverSet,
            MuleContext muleContext,
            ExtensionManagerAdapter extensionManager,
            ExpirationPolicy expirationPolicy) throws Exception;
}
