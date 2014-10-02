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

package com.consol.citrus.message;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.springframework.beans.SimpleTypeConverter;

import java.util.*;

/**
 * Default message implementation holds message payload and message headers. Also provides access methods for special
 * header elements such as unique message id and creation timestamp.
 *
 * @author Christoph Deppisch
 * @since 2.0
 */
public class DefaultMessage implements Message {

    /** Message payload object */
    private Object payload;

    /** Message headers */
    private final Map<String, Object> headers;

    /**
     * Default constructor usiong just message payload.
     * @param payload
     */
    public DefaultMessage(Object payload) {
        this(payload, new LinkedHashMap<String, Object>());
    }

    /**
     * Default constructor using payload and headers.
     * @param payload
     * @param headers
     */
    public DefaultMessage(Object payload, Map<String, Object> headers) {
        this.payload = payload;
        this.headers = headers;

        this.headers.put(MessageHeaders.ID, UUID.randomUUID());
        this.headers.put(MessageHeaders.TIMESTAMP, System.currentTimeMillis());
    }

    @Override
    public String getId() {
        return headers.get(MessageHeaders.ID).toString();
    }

    /**
     * Gets the message creation timestamp;
     * @return
     */
    public Long getTimestamp() {
        return (Long) headers.get(MessageHeaders.TIMESTAMP);
    }

    @Override
    public String toString() {
        return String.format("%s(%s) [payload: %s][headers: %s]", getClass().getSimpleName(), getId(), payload, headers);
    }

    @Override
    public DefaultMessage setHeader(String headerName, Object headerValue) {
        if (headerName.equals(MessageHeaders.ID)) {
            throw new CitrusRuntimeException("Not allowed to set reserved message header: " + MessageHeaders.ID);
        }

        headers.put(headerName, headerValue);
        return this;
    }

    @Override
    public Object getHeader(String headerName) {
        return headers.get(headerName);
    }

    @Override
    public void removeHeader(String headerName) {
        if (headerName.equals(MessageHeaders.ID)) {
            throw new CitrusRuntimeException("Not allowed to remove reserved message header from message: " + MessageHeaders.ID);
        }

        headers.remove(headerName);
    }

    @Override
    public <T> T getPayload(Class<T> type) {
        if (type.isInstance(payload)) {
            return type.cast(payload);
        }

        return new SimpleTypeConverter().convertIfNecessary(payload, type);
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    @Override
    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @Override
    public Map<String, Object> copyHeaders() {
        LinkedHashMap copy = new LinkedHashMap(headers.size());
        copy.putAll(headers);
        return copy;
    }
}
