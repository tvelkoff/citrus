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

package com.consol.citrus.config.xml;

import com.consol.citrus.TestActor;
import com.consol.citrus.channel.ChannelEndpoint;
import com.consol.citrus.testng.AbstractBeanDefinitionParserTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * @author Christoph Deppisch
 */
public class ChannelEndpointParserTest extends AbstractBeanDefinitionParserTest {

    @Test
    public void testChannelEndpointParser() {
        Map<String, ChannelEndpoint> endpoints = beanDefinitionContext.getBeansOfType(ChannelEndpoint.class);

        Assert.assertEquals(endpoints.size(), 4);

        // 1st message receiver
        ChannelEndpoint channelEndpoint = endpoints.get("channelEndpoint1");
        Assert.assertEquals(channelEndpoint.getEndpointConfiguration().getChannelName(), "channelName");
        Assert.assertNull(channelEndpoint.getEndpointConfiguration().getChannel());
        Assert.assertEquals(channelEndpoint.getEndpointConfiguration().getTimeout(), 5000L);
        Assert.assertNotNull(channelEndpoint.getEndpointConfiguration().getChannelResolver());
        Assert.assertEquals(channelEndpoint.getEndpointConfiguration().isUseObjectMessages(), false);

        // 2nd message receiver
        channelEndpoint = endpoints.get("channelEndpoint2");
        Assert.assertNull(channelEndpoint.getEndpointConfiguration().getChannelName());
        Assert.assertNotNull(channelEndpoint.getEndpointConfiguration().getChannel());
        Assert.assertEquals(channelEndpoint.getEndpointConfiguration().getTimeout(), 10000L);
        Assert.assertNull(channelEndpoint.getEndpointConfiguration().getChannelResolver());

        // 3rd message receiver
        channelEndpoint = endpoints.get("channelEndpoint3");
        Assert.assertNull(channelEndpoint.getEndpointConfiguration().getChannelName());
        Assert.assertNull(channelEndpoint.getEndpointConfiguration().getChannel());
        Assert.assertNull(channelEndpoint.getEndpointConfiguration().getChannelResolver());

        // 4th message receiver
        channelEndpoint = endpoints.get("channelEndpoint4");
        Assert.assertNotNull(channelEndpoint.getActor());
        Assert.assertEquals(channelEndpoint.getEndpointConfiguration().isUseObjectMessages(), true);
        Assert.assertEquals(channelEndpoint.getActor(), beanDefinitionContext.getBean("testActor", TestActor.class));
    }
}
