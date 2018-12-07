/*
 * Copyright 2006-2012 the original author or authors.
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

package com.consol.citrus.actions;

import com.consol.citrus.testng.AbstractTestNGUnitTest;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessageSelector;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.Mockito.*;

/**
 * @author Christoph Deppisch
 */
public class PurgeMessageChannelActionTest extends AbstractTestNGUnitTest {
	
    @Autowired
    @Qualifier(value="mockChannel")
    private QueueChannel mockChannel;
    
    private QueueChannel emptyChannel = Mockito.mock(QueueChannel.class);
    
    @Test
    public void testPurgeWithChannelNames() throws Exception {
        PurgeMessageChannelAction purgeChannelAction = new PurgeMessageChannelAction();
        purgeChannelAction.setBeanFactory(applicationContext);
        purgeChannelAction.afterPropertiesSet();
        
        List<String> channelNames = new ArrayList<String>();
        channelNames.add("mockChannel");
        purgeChannelAction.setChannelNames(channelNames);
        
        List<Message<?>> purgedMessages = new ArrayList<Message<?>>();
        purgedMessages.add(MessageBuilder.withPayload("<TestRequest>Hello World!</TestRequest>").build());
        
        reset(mockChannel);
        
        when(mockChannel.purge((MessageSelector)any())).thenReturn(purgedMessages);

        
        purgeChannelAction.execute(context);

    }
    
	@SuppressWarnings("unchecked")
    @Test
    public void testPurgeWithChannelObjects() throws Exception {
	    PurgeMessageChannelAction purgeChannelAction = new PurgeMessageChannelAction();
        purgeChannelAction.setBeanFactory(applicationContext);
        purgeChannelAction.afterPropertiesSet();
        
        List<MessageChannel> channels = new ArrayList<MessageChannel>();
        channels.add(mockChannel);
        channels.add(emptyChannel);
        purgeChannelAction.setChannels(channels);
        
        List<Message<?>> purgedMessages = new ArrayList<Message<?>>();
        purgedMessages.add(MessageBuilder.withPayload("<TestRequest>Hello World!</TestRequest>").build());
        
        reset(mockChannel, emptyChannel);
        
        when(mockChannel.purge((MessageSelector)any())).thenReturn(purgedMessages);
        when(emptyChannel.purge((MessageSelector)any())).thenReturn(Collections.EMPTY_LIST);

        
        purgeChannelAction.execute(context);

    }
	
	@Test
    public void testPurgeWithMessageSelector() throws Exception {
        PurgeMessageChannelAction purgeChannelAction = new PurgeMessageChannelAction();
        purgeChannelAction.setBeanFactory(applicationContext);
        purgeChannelAction.afterPropertiesSet();
        
        MessageSelector messageSelector = new MessageSelector() {
            public boolean accept(Message message) {
                return false;
            }
        };
        
        purgeChannelAction.setMessageSelector(messageSelector);
        
        List<MessageChannel> channels = new ArrayList<MessageChannel>();
        channels.add(mockChannel);
        purgeChannelAction.setChannels(channels);
        
        List<Message<?>> purgedMessages = new ArrayList<Message<?>>();
        purgedMessages.add(MessageBuilder.withPayload("<TestRequest>Hello World!</TestRequest>").build());
        
        reset(mockChannel);
        
        when(mockChannel.purge(messageSelector)).thenReturn(purgedMessages);

        
        purgeChannelAction.execute(context);

    }
	
}
