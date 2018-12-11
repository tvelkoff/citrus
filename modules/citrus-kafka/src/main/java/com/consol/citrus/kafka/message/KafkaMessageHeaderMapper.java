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

package com.consol.citrus.kafka.message;

import com.consol.citrus.util.TypeConversionUtils;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;

import java.util.HashMap;
import java.util.Map;

/**
 * Citrus Kafka header mapper translates internal message headers to Spring integration message headers and
 * vice versa.
 *
 * @author Christoph Deppisch
 * @since 2.8
 */
public class KafkaMessageHeaderMapper {

    public Map<String, Object> fromHeaders(Headers kafkaHeaders) {
        Map<String, Object> headers = new HashMap<>();
        kafkaHeaders.forEach(header -> headers.put(header.key(), new String(header.value())));

        return headers;
    }

    public Headers toHeaders(Map<String, Object> headers) {
        Headers kafkaHeaders = new RecordHeaders();

        for (Map.Entry<String, Object> headerEntry : headers.entrySet()) {
            if (!headerEntry.getKey().startsWith(KafkaMessageHeaders.KAFKA_PREFIX)
                    && !headerEntry.getKey().equals(com.consol.citrus.message.MessageHeaders.ID)
                    && !headerEntry.getKey().equals(com.consol.citrus.message.MessageHeaders.TIMESTAMP)) {
                kafkaHeaders.add(headerEntry.getKey(), TypeConversionUtils.convertIfNecessary(headerEntry.getValue(), byte[].class));
            }
        }

        return kafkaHeaders;
    }
}
