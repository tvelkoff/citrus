/*
 * Copyright 2006-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.dsl.endpoint;

import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import com.consol.citrus.testng.AbstractTestNGUnitTest;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 * @since 1.3.1
 */
public class TestExecutingEndpointAdapterTest extends AbstractTestNGUnitTest {
    private TestExecutingEndpointAdapter endpointAdapter;

    @BeforeClass
    public void loadContext() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] {"classpath:com/consol/citrus/dsl/endpoint/TestExecutingEndpointAdapterTest-context.xml"}, applicationContext);
        endpointAdapter = ctx.getBean(TestExecutingEndpointAdapter.class);
    }

    /**
     * Test for handler routing by node content
     */
    @Test
    public void testRouteMessageByElementTextContent() throws Exception {
        XPathPayloadMappingKeyExtractor mappingNameExtractor = new XPathPayloadMappingKeyExtractor();
        mappingNameExtractor.setXpathExpression("//TestDesigner/@name");
        endpointAdapter.setMappingKeyExtractor(mappingNameExtractor);

        Message response = endpointAdapter.handleMessage(
                new DefaultMessage("<TestDesigner name=\"FooTestDesigner\"></TestDesigner>"));

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getPayload(), "<TestDesigner name=\"FooTestDesigner\">OK</TestDesigner>");

        response = endpointAdapter.handleMessage(
                new DefaultMessage("<TestDesigner name=\"BarTestDesigner\"></TestDesigner>"));

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getPayload(), "<TestDesigner name=\"BarTestDesigner\">OK</TestDesigner>");

        mappingNameExtractor.setXpathExpression("//TestRunner/@name");

        response = endpointAdapter.handleMessage(
                new DefaultMessage("<TestRunner name=\"FooTestRunner\"></TestRunner>"));

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getPayload(), "<TestRunner name=\"FooTestRunner\">OK</TestRunner>");

        response = endpointAdapter.handleMessage(
                new DefaultMessage("<TestRunner name=\"BarTestRunner\"></TestRunner>"));

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getPayload(), "<TestRunner name=\"BarTestRunner\">OK</TestRunner>");
    }

    /**
     * Test for handler routing without Xpath given (implementation takes the value of first node).
     */
    @Test
    public void testRouteMessageWithDefaultXpath() throws Exception {
        XPathPayloadMappingKeyExtractor mappingNameExtractor = new XPathPayloadMappingKeyExtractor();
        endpointAdapter.setMappingKeyExtractor(mappingNameExtractor);

        Message response = endpointAdapter.handleMessage(
                new DefaultMessage(
                        "<FooBarTestDesigner></FooBarTestDesigner>"));

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getPayload(), "<FooBarTestDesigner>OK</FooBarTestDesigner>");

        response = endpointAdapter.handleMessage(
                new DefaultMessage(
                        "<FooBarTestRunner></FooBarTestRunner>"));

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getPayload(), "<FooBarTestRunner>OK</FooBarTestRunner>");
    }

    /**
     * Test for Xpath which is not found --> shall raise exception
     */
    @Test
    public void testRouteMessageWithBadXpathExpression() throws Exception {
        XPathPayloadMappingKeyExtractor mappingNameExtractor = new XPathPayloadMappingKeyExtractor();
        mappingNameExtractor.setXpathExpression("//I_DO_NOT_EXIST");
        endpointAdapter.setMappingKeyExtractor(mappingNameExtractor);

        try {
            endpointAdapter.handleMessage(new DefaultMessage(
                    "<FooTestDesigner>foo test please</FooTestDesigner>"));
            Assert.fail("Missing exception due to bad XPath expression");
        } catch (CitrusRuntimeException e) {
            Assert.assertEquals(e.getMessage(), "No result for XPath expression: '//I_DO_NOT_EXIST'");
        }
    }

    /**
     * Test for correct xpath, but no handler bean is found --> shall raise exc
     */
    @Test
    public void testRouteMessageWithBadHandlerConfiguration() throws Exception {
        XPathPayloadMappingKeyExtractor mappingNameExtractor = new XPathPayloadMappingKeyExtractor();
        mappingNameExtractor.setXpathExpression("//TestDesigner/@name");
        endpointAdapter.setMappingKeyExtractor(mappingNameExtractor);

        try {
            endpointAdapter.handleMessage(new DefaultMessage(
                    "<TestDesigner name=\"UNKNOWN_TEST\"></TestDesigner>"));
            Assert.fail("Missing exception due to unknown endpoint adapter");
        } catch (CitrusRuntimeException e) {
            Assert.assertTrue(e.getCause() instanceof NoSuchBeanDefinitionException);
        }
    }
}
