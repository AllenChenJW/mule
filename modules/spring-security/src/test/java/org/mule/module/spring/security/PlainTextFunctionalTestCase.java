/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.springconfig.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.api.config.MuleProperties;
import org.mule.security.MuleCredentials;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.transport.http.HttpConnector;
import org.mule.transport.http.HttpConstants;

import org.junit.Test;

public class PlainTextFunctionalTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigFile()
    {
        // Note that this file contains global attributes, which the configuration-building
        // process will ignore (MULE-5375)
        return "encryption-test-flow.xml";
    }

    @Test
    public void testAuthenticationFailureNoContext() throws Exception
    {
        org.mule.api.client.MuleClient client = muleContext.getClient();
        MuleMessage m = client.send("http://localhost:4567/index.html", "", null);
        assertNotNull(m);
        int status = m.getInboundProperty(HttpConnector.HTTP_STATUS_PROPERTY, -1);
        assertEquals(HttpConstants.SC_UNAUTHORIZED, status);
    }

    @Test
    public void testAuthenticationFailureBadCredentials() throws Exception
    {
        MuleClient client = muleContext.getClient();

        MuleMessage message = createRequestMessage("anonX", "anonX");
        MuleMessage response = client.send("http://localhost:4567/index.html", message);
        assertNotNull(response);
        int status = response.getInboundProperty(HttpConnector.HTTP_STATUS_PROPERTY, -1);
        assertEquals(HttpConstants.SC_UNAUTHORIZED, status);
    }

    @Test
    public void testAuthenticationAuthorised() throws Exception
    {
        MuleClient client = muleContext.getClient();

        MuleMessage message = createRequestMessage("anon", "anon");
        MuleMessage response = client.send("http://localhost:4567/index.html", message);
        assertNotNull(response);
        int status = response.getInboundProperty(HttpConnector.HTTP_STATUS_PROPERTY, -1);
        assertEquals(HttpConstants.SC_OK, status);
    }

    private MuleMessage createRequestMessage(String user, String password)
    {
        MuleMessage message = new DefaultMuleMessage("", muleContext);
        String header = MuleCredentials.createHeader(user, password.toCharArray());
        message.setOutboundProperty(MuleProperties.MULE_USER_PROPERTY, header);
        return message;
    }
}
