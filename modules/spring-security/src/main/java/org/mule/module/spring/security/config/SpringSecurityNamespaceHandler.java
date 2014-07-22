/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.springconfig.security.config;

import org.mule.module.springconfig.parsers.collection.ChildMapEntryDefinitionParser;
import org.mule.module.springconfig.parsers.generic.ChildDefinitionParser;
import org.mule.module.springconfig.parsers.specific.SecurityFilterDefinitionParser;
import org.mule.module.springconfig.security.AuthorizationFilter;
import org.mule.module.springconfig.security.SpringProviderAdapter;
import org.mule.module.springconfig.security.filters.http.HttpBasicAuthenticationFilter;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Registers a Bean Definition Parser for handling Spring Security related elements.
 */
public class SpringSecurityNamespaceHandler extends NamespaceHandlerSupport
{
    public void init()
    {
        registerBeanDefinitionParser("security-manager", new SecurityManagerDefinitionParser());
        registerBeanDefinitionParser("delegate-security-provider", new ChildDefinitionParser("provider", SpringProviderAdapter.class));
        SecurityFilterDefinitionParser securityFilterDefinitionParser = new SecurityFilterDefinitionParser(HttpBasicAuthenticationFilter.class);
        securityFilterDefinitionParser.addAlias("securityManager-ref", "securityManager");
        registerBeanDefinitionParser("http-security-filter", securityFilterDefinitionParser);
        registerBeanDefinitionParser("authorization-filter", new SecurityFilterDefinitionParser(AuthorizationFilter.class));
        registerBeanDefinitionParser("security-property", new ChildMapEntryDefinitionParser("securityProperty", "name", "value"));
    }

}
