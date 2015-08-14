/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.config;

import org.mule.config.spring.handlers.AbstractMuleNamespaceHandler;
import org.mule.config.spring.parsers.generic.ChildDefinitionParser;

public class ExtensionConfigNamespaceHandler extends AbstractMuleNamespaceHandler
{
    @Override
    public void init()
    {
        registerBeanDefinitionParser("extensions-config", new ChildDefinitionParser("extension", ExtensionConfig.class));
        registerBeanDefinitionParser("dynamic-configuration-expiration", new ChildDefinitionParser("dynamicConfigExpirationFrequency", DynamicConfigurationExpirationFactoryBean.class));
    }
}
