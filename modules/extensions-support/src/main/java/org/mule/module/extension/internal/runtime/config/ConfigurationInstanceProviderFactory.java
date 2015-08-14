/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
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
