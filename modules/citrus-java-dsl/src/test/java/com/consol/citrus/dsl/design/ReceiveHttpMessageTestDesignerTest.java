/*
 * Copyright 2006-2014 the original author or authors.
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

package com.consol.citrus.dsl.design;

import com.consol.citrus.TestCase;
import com.consol.citrus.actions.ReceiveMessageAction;
import com.consol.citrus.dsl.actions.DelegatingTestAction;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageContentBuilder;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.consol.citrus.http.server.HttpServer;
import com.consol.citrus.testng.AbstractTestNGUnitTest;
import com.consol.citrus.validation.context.HeaderValidationContext;
import com.consol.citrus.validation.json.JsonMessageValidationContext;
import com.consol.citrus.validation.xml.XmlMessageValidationContext;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
public class ReceiveHttpMessageTestDesignerTest extends AbstractTestNGUnitTest {

    private HttpClient httpClient = Mockito.mock(HttpClient.class);
    private HttpServer httpServer = Mockito.mock(HttpServer.class);

    @Test
    public void testHttpRequestProperties() {
        MockTestDesigner builder = new MockTestDesigner(applicationContext, context) {
            @Override
            public void configure() {
                http().server(httpServer)
                        .receive()
                        .get("/test/foo")
                        .method(HttpMethod.GET)
                        .queryParam("param1", "value1")
                        .queryParam("param2", "value2")
                        .payload("<TestRequest><Message>Hello World!</Message></TestRequest>");
            }
        };

        builder.configure();

        TestCase test = builder.getTestCase();
        Assert.assertEquals(test.getActionCount(), 1);
        Assert.assertEquals(test.getActions().get(0).getClass(), DelegatingTestAction.class);
        Assert.assertEquals(((DelegatingTestAction)test.getActions().get(0)).getDelegate().getClass(), ReceiveMessageAction.class);

        ReceiveMessageAction action = (ReceiveMessageAction) ((DelegatingTestAction)test.getActions().get(0)).getDelegate();
        Assert.assertEquals(action.getName(), "receive");

        Assert.assertEquals(action.getEndpoint(), httpServer);
        Assert.assertEquals(action.getValidationContexts().size(), 3L);
        Assert.assertEquals(action.getValidationContexts().get(0).getClass(), HeaderValidationContext.class);
        Assert.assertEquals(action.getValidationContexts().get(1).getClass(), XmlMessageValidationContext.class);
        Assert.assertEquals(action.getValidationContexts().get(2).getClass(), JsonMessageValidationContext.class);

        HttpMessageContentBuilder messageBuilder = (HttpMessageContentBuilder) action.getMessageBuilder();
        Assert.assertEquals(messageBuilder.getMessage().getPayload(), "<TestRequest><Message>Hello World!</Message></TestRequest>");
        Assert.assertEquals(messageBuilder.getMessage().getHeaders().size(), 7L);
        Assert.assertEquals(messageBuilder.getMessage().getHeaders().get(HttpMessageHeaders.HTTP_REQUEST_METHOD), HttpMethod.GET.name());
        Assert.assertEquals(messageBuilder.getMessage().getHeaders().get(HttpMessageHeaders.HTTP_REQUEST_URI), "/test/foo");
        Assert.assertEquals(messageBuilder.getMessage().getHeaders().get(HttpMessageHeaders.HTTP_QUERY_PARAMS), "param1=value1,param2=value2");
    }

    @Test
    public void testHttpResponseProperties() {
        MockTestDesigner builder = new MockTestDesigner(applicationContext, context) {
            @Override
            public void configure() {
                http().client(httpClient)
                        .receive()
                        .response(HttpStatus.OK)
                        .version("HTTP/1.1")
                        .payload("<TestRequest><Message>Hello World!</Message></TestRequest>");
            }
        };

        builder.configure();

        TestCase test = builder.getTestCase();
        Assert.assertEquals(test.getActionCount(), 1);
        Assert.assertEquals(test.getActions().get(0).getClass(), DelegatingTestAction.class);
        Assert.assertEquals(((DelegatingTestAction)test.getActions().get(0)).getDelegate().getClass(), ReceiveMessageAction.class);

        ReceiveMessageAction action = (ReceiveMessageAction) ((DelegatingTestAction)test.getActions().get(0)).getDelegate();
        Assert.assertEquals(action.getName(), "receive");

        Assert.assertEquals(action.getEndpoint(), httpClient);
        Assert.assertEquals(action.getValidationContexts().size(), 3L);
        Assert.assertEquals(action.getValidationContexts().get(0).getClass(), HeaderValidationContext.class);
        Assert.assertEquals(action.getValidationContexts().get(1).getClass(), XmlMessageValidationContext.class);
        Assert.assertEquals(action.getValidationContexts().get(2).getClass(), JsonMessageValidationContext.class);

        HttpMessageContentBuilder messageBuilder = (HttpMessageContentBuilder) action.getMessageBuilder();
        Assert.assertEquals(messageBuilder.getMessage().getPayload(), "<TestRequest><Message>Hello World!</Message></TestRequest>");
        Assert.assertEquals(messageBuilder.getMessage().getHeaders().size(), 5L);
        Assert.assertEquals(messageBuilder.getMessage().getHeaders().get(HttpMessageHeaders.HTTP_STATUS_CODE), 200);
        Assert.assertEquals(messageBuilder.getMessage().getHeaders().get(HttpMessageHeaders.HTTP_REASON_PHRASE), "OK");
        Assert.assertEquals(messageBuilder.getMessage().getHeaders().get(HttpMessageHeaders.HTTP_VERSION), "HTTP/1.1");
    }

}
