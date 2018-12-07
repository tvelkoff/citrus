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
import com.consol.citrus.actions.SendMessageAction;
import com.consol.citrus.dsl.actions.DelegatingTestAction;
import com.consol.citrus.endpoint.resolver.DynamicEndpointUriResolver;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageContentBuilder;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.testng.AbstractTestNGUnitTest;
import com.consol.citrus.validation.builder.StaticMessageContentBuilder;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * @author Christoph Deppisch
 */
public class SendHttpMessageTestDesignerTest extends AbstractTestNGUnitTest {

    private HttpClient httpClient = Mockito.mock(HttpClient.class);
    private ApplicationContext applicationContextMock = Mockito.mock(ApplicationContext.class);

    @Test
    public void testFork() {
        MockTestDesigner builder = new MockTestDesigner(applicationContext, context) {
            @Override
            public void configure() {
                send(httpClient)
                        .message(new DefaultMessage("Foo").setHeader("operation", "foo"))
                            .header("additional", "additionalValue");

                send(httpClient)
                        .message(new DefaultMessage("Foo").setHeader("operation", "foo"))
                        .fork(true);
            }
        };

        builder.configure();

        TestCase test = builder.getTestCase();
        Assert.assertEquals(test.getActionCount(), 2);
        Assert.assertEquals(test.getActions().get(0).getClass(), DelegatingTestAction.class);
        Assert.assertEquals(((DelegatingTestAction)test.getActions().get(0)).getDelegate().getClass(), SendMessageAction.class);
        Assert.assertEquals(test.getActions().get(1).getClass(), DelegatingTestAction.class);
        Assert.assertEquals(((DelegatingTestAction)test.getActions().get(1)).getDelegate().getClass(), SendMessageAction.class);

        SendMessageAction action = (SendMessageAction) ((DelegatingTestAction)test.getActions().get(0)).getDelegate();
        Assert.assertEquals(action.getName(), "send");

        Assert.assertEquals(action.getEndpoint(), httpClient);
        Assert.assertEquals(action.getMessageBuilder().getClass(), StaticMessageContentBuilder.class);

        StaticMessageContentBuilder messageBuilder = (StaticMessageContentBuilder) action.getMessageBuilder();
        Assert.assertEquals(messageBuilder.getMessage().getPayload(String.class), "Foo");
        Assert.assertEquals(messageBuilder.getMessage().getHeader("operation"), "foo");
        Assert.assertEquals(messageBuilder.getMessageHeaders().size(), 1L);
        Assert.assertEquals(messageBuilder.getMessageHeaders().get("additional"), "additionalValue");

        Assert.assertFalse(action.isForkMode());

        action = (SendMessageAction) ((DelegatingTestAction)test.getActions().get(1)).getDelegate();
        Assert.assertEquals(action.getName(), "send");

        Assert.assertEquals(action.getEndpoint(), httpClient);
        Assert.assertEquals(action.getMessageBuilder().getClass(), StaticMessageContentBuilder.class);

        Assert.assertTrue(action.isForkMode());
    }

    @Test
    public void testHttpMethod() {
        MockTestDesigner builder = new MockTestDesigner(applicationContext, context) {
            @Override
            public void configure() {
                http().client(httpClient)
                        .send()
                        .get()
                        .payload("<TestRequest><Message>Hello World!</Message></TestRequest>");
            }
        };

        builder.configure();

        TestCase test = builder.getTestCase();
        Assert.assertEquals(test.getActionCount(), 1);
        Assert.assertEquals(test.getActions().get(0).getClass(), DelegatingTestAction.class);
        Assert.assertEquals(((DelegatingTestAction)test.getActions().get(0)).getDelegate().getClass(), SendMessageAction.class);

        SendMessageAction action = (SendMessageAction) ((DelegatingTestAction)test.getActions().get(0)).getDelegate();
        Assert.assertEquals(action.getName(), "send");

        Assert.assertEquals(action.getEndpoint(), httpClient);
        Assert.assertEquals(action.getMessageBuilder().getClass(), HttpMessageContentBuilder.class);

        HttpMessageContentBuilder messageBuilder = (HttpMessageContentBuilder) action.getMessageBuilder();
        Assert.assertEquals(messageBuilder.getMessage().getPayload(), "<TestRequest><Message>Hello World!</Message></TestRequest>");
        Assert.assertEquals(messageBuilder.getMessage().getHeaders().size(), 3L);
        Assert.assertEquals(messageBuilder.getMessage().getHeaders().get(HttpMessageHeaders.HTTP_REQUEST_METHOD), HttpMethod.GET.name());
    }

    @Test
    public void testHttpRequestUriAndPath() {
        MockTestDesigner builder = new MockTestDesigner(applicationContext, context) {
            @Override
            public void configure() {
                http().client(httpClient)
                        .send()
                        .get("/test")
                        .uri("http://localhost:8080/")
                        .payload("<TestRequest><Message>Hello World!</Message></TestRequest>");
            }
        };

        builder.configure();

        TestCase test = builder.getTestCase();
        Assert.assertEquals(test.getActionCount(), 1);
        Assert.assertEquals(test.getActions().get(0).getClass(), DelegatingTestAction.class);
        Assert.assertEquals(((DelegatingTestAction)test.getActions().get(0)).getDelegate().getClass(), SendMessageAction.class);

        SendMessageAction action = (SendMessageAction) ((DelegatingTestAction)test.getActions().get(0)).getDelegate();
        Assert.assertEquals(action.getName(), "send");

        Assert.assertEquals(action.getEndpoint(), httpClient);
        Assert.assertEquals(action.getMessageBuilder().getClass(), HttpMessageContentBuilder.class);

        HttpMessageContentBuilder messageBuilder = (HttpMessageContentBuilder) action.getMessageBuilder();
        Assert.assertEquals(messageBuilder.getMessage().getPayload(), "<TestRequest><Message>Hello World!</Message></TestRequest>");
        Assert.assertEquals(messageBuilder.getMessage().getHeaders().size(), 6L);
        Assert.assertEquals(messageBuilder.getMessage().getHeaders().get(HttpMessageHeaders.HTTP_REQUEST_URI), "http://localhost:8080/");
        Assert.assertEquals(messageBuilder.getMessage().getHeaders().get(DynamicEndpointUriResolver.ENDPOINT_URI_HEADER_NAME), "http://localhost:8080/");
        Assert.assertEquals(messageBuilder.getMessage().getHeaders().get(DynamicEndpointUriResolver.REQUEST_PATH_HEADER_NAME), "/test");
    }

    @Test
    public void testHttpRequestUriAndQueryParams() {
        MockTestDesigner builder = new MockTestDesigner(applicationContext, context) {
            @Override
            public void configure() {
                http().client(httpClient)
                        .send()
                        .get()
                        .uri("http://localhost:8080/")
                        .queryParam("param1", "value1")
                        .queryParam("param2", "value2")
                        .payload("<TestRequest><Message>Hello World!</Message></TestRequest>");
            }
        };

        builder.configure();

        TestCase test = builder.getTestCase();
        Assert.assertEquals(test.getActionCount(), 1);
        Assert.assertEquals(test.getActions().get(0).getClass(), DelegatingTestAction.class);
        Assert.assertEquals(((DelegatingTestAction)test.getActions().get(0)).getDelegate().getClass(), SendMessageAction.class);

        SendMessageAction action = (SendMessageAction) ((DelegatingTestAction)test.getActions().get(0)).getDelegate();
        Assert.assertEquals(action.getName(), "send");

        Assert.assertEquals(action.getEndpoint(), httpClient);
        Assert.assertEquals(action.getMessageBuilder().getClass(), HttpMessageContentBuilder.class);

        HttpMessageContentBuilder messageBuilder = (HttpMessageContentBuilder) action.getMessageBuilder();
        Assert.assertEquals(messageBuilder.getMessage().getPayload(), "<TestRequest><Message>Hello World!</Message></TestRequest>");
        Assert.assertEquals(messageBuilder.getMessage().getHeaders().size(), 7L);
        Assert.assertEquals(messageBuilder.getMessage().getHeaders().get(HttpMessageHeaders.HTTP_REQUEST_URI), "http://localhost:8080/");
        Assert.assertEquals(messageBuilder.getMessage().getHeaders().get(DynamicEndpointUriResolver.ENDPOINT_URI_HEADER_NAME), "http://localhost:8080/");
        Assert.assertEquals(messageBuilder.getMessage().getHeaders().get(DynamicEndpointUriResolver.QUERY_PARAM_HEADER_NAME), "param1=value1,param2=value2");
    }

}
