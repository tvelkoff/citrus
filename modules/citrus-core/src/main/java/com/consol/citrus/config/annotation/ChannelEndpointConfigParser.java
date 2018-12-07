/*
 * Copyright 2006-2018 the original author or authors.
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

package com.consol.citrus.config.annotation;

import com.consol.citrus.TestActor;
import com.consol.citrus.channel.*;
import com.consol.citrus.context.ReferenceResolver;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.core.DestinationResolver;
import org.springframework.util.StringUtils;

/**
 * @author Christoph Deppisch
 * @since 2.7.6
 */
public class ChannelEndpointConfigParser extends AbstractAnnotationConfigParser<ChannelEndpointConfig, ChannelEndpoint> {

    /**
     * Constructor matching super.
     * @param referenceResolver
     */
    public ChannelEndpointConfigParser(ReferenceResolver referenceResolver) {
        super(referenceResolver);
    }

    @Override
    public ChannelEndpoint parse(ChannelEndpointConfig annotation) {
        ChannelEndpointBuilder builder = new ChannelEndpointBuilder();

        String channel = annotation.channel();
        String channelName = annotation.channelName();

        if (StringUtils.hasText(channel)) {
            builder.channel(getReferenceResolver().resolve(annotation.channel(), MessageChannel.class));
        }

        if (StringUtils.hasText(channelName)) {
            builder.channel(annotation.channelName());
        }

        if (StringUtils.hasText(annotation.messagingTemplate())) {
            //messagingTemplate
            String messagingTemplate = "messagingTemplate"; //default value

            if (StringUtils.hasText(annotation.messagingTemplate())) {
                messagingTemplate = annotation.messagingTemplate();
            }

            builder.messagingTemplate(getReferenceResolver().resolve(messagingTemplate, MessagingTemplate.class));
        }

        builder.useObjectMessages(annotation.useObjectMessages());

        if (StringUtils.hasText(annotation.messageConverter())) {
            builder.messageConverter(getReferenceResolver().resolve(annotation.messageConverter(), ChannelMessageConverter.class));
        }

        if (StringUtils.hasText(annotation.channelResolver())) {
            builder.channelResolver(getReferenceResolver().resolve(annotation.channelResolver(), DestinationResolver.class));
        }

        builder.timeout(annotation.timeout());

        if (StringUtils.hasText(annotation.actor())) {
            builder.actor(getReferenceResolver().resolve(annotation.actor(), TestActor.class));
        }

        return builder.initialize().build();
    }
}
