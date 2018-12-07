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

package com.consol.citrus.camel.endpoint;

import com.consol.citrus.camel.message.CitrusCamelMessageHeaders;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.ActionTimeoutException;
import com.consol.citrus.message.Message;
import com.consol.citrus.message.MessageHeaders;
import com.consol.citrus.message.correlation.CorrelationManager;
import com.consol.citrus.message.correlation.PollingCorrelationManager;
import com.consol.citrus.messaging.ReplyProducer;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author Christoph Deppisch
 * @since 1.4.1
 */
public class CamelSyncConsumer extends CamelConsumer implements ReplyProducer {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(CamelSyncConsumer.class);

    /** Storage for in flight exchanges */
    private CorrelationManager<Exchange> correlationManager;

    /** Endpoint configuration */
    private final CamelSyncEndpointConfiguration endpointConfiguration;

    /**
     * Constructor using endpoint configuration and fields.
     * @param name
     * @param endpointConfiguration
     */
    public CamelSyncConsumer(String name, CamelSyncEndpointConfiguration endpointConfiguration) {
        super(name, endpointConfiguration);
        this.endpointConfiguration = endpointConfiguration;

        this.correlationManager = new PollingCorrelationManager<>(endpointConfiguration, "Camel exchange not set up yet");
    }

    @Override
    public Message receive(TestContext context, long timeout) {
        if (log.isDebugEnabled()) {
            log.debug("Receiving message from camel endpoint: '" + endpointConfiguration.getEndpointUri() + "'");
        }

        Exchange exchange = getConsumerTemplate().receive(endpointConfiguration.getEndpointUri(), timeout);

        if (exchange == null) {
            throw new ActionTimeoutException("Action timed out while receiving message from camel endpoint '" + endpointConfiguration.getEndpointUri() + "'");
        }

        log.info("Received message from camel endpoint: '" + endpointConfiguration.getEndpointUri() + "'");

        Message message = endpointConfiguration.getMessageConverter().convertInbound(exchange, endpointConfiguration, context);
        context.onInboundMessage(message);

        String correlationKeyName = endpointConfiguration.getCorrelator().getCorrelationKeyName(getName());
        String correlationKey = endpointConfiguration.getCorrelator().getCorrelationKey(message);
        correlationManager.saveCorrelationKey(correlationKeyName, correlationKey, context);
        correlationManager.store(correlationKey, exchange);

        return message;
    }

    @Override
    public void send(Message message, TestContext context) {
        Assert.notNull(message, "Message is empty - unable to send empty message");

        String correlationKeyName = endpointConfiguration.getCorrelator().getCorrelationKeyName(getName());
        String correlationKey = correlationManager.getCorrelationKey(correlationKeyName, context);
        Exchange exchange = correlationManager.find(correlationKey, endpointConfiguration.getTimeout());
        Assert.notNull(exchange, "Failed to find camel exchange for message correlation key: '" + correlationKey + "'");

        buildOutMessage(exchange, message);

        if (log.isDebugEnabled()) {
            log.debug("Sending reply message to camel endpoint: '" + exchange.getFromEndpoint() + "'");
        }

        getConsumerTemplate().doneUoW(exchange);

        context.onOutboundMessage(message);

        log.info("Message was sent to camel endpoint: '" + exchange.getFromEndpoint() + "'");
    }

    /**
     * Builds response and sets it as out message on given Camel exchange.
     * @param message
     * @param exchange
     * @return
     */
    private void buildOutMessage(Exchange exchange, Message message) {
        org.apache.camel.Message reply = exchange.getOut();
        for (Map.Entry<String, Object> header : message.getHeaders().entrySet()) {
            if (!header.getKey().startsWith(MessageHeaders.PREFIX)) {
                reply.setHeader(header.getKey(), header.getValue());
            }
        }

        if (message.getHeader(CitrusCamelMessageHeaders.EXCHANGE_EXCEPTION) != null) {
            String exceptionClass = message.getHeader(CitrusCamelMessageHeaders.EXCHANGE_EXCEPTION).toString();
            String exceptionMsg = null;

            if (message.getHeader(CitrusCamelMessageHeaders.EXCHANGE_EXCEPTION_MESSAGE) != null) {
                exceptionMsg = message.getHeader(CitrusCamelMessageHeaders.EXCHANGE_EXCEPTION_MESSAGE).toString();
            }

            try {
                Class<?> exception = Class.forName(exceptionClass);
                if (exceptionMsg != null) {
                    exchange.setException((Throwable) exception.getConstructor(String.class).newInstance(exceptionMsg));
                } else {
                    exchange.setException((Throwable) exception.newInstance());
                }
            } catch (RuntimeException e) {
                log.warn("Unable to create proper exception instance for exchange!", e);
            } catch (Exception e) {
                log.warn("Unable to create proper exception instance for exchange!", e);
            }
        }

        reply.setBody(message.getPayload());
    }

    /**
     * Sets the correlation manager.
     * @param correlationManager
     */
    public void setCorrelationManager(CorrelationManager<Exchange> correlationManager) {
        this.correlationManager = correlationManager;
    }

}
