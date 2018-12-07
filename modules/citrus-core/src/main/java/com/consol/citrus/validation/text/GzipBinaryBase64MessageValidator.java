/*
 * Copyright 2006-2016 the original author or authors.
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

package com.consol.citrus.validation.text;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.message.Message;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.validation.context.ValidationContext;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * Message validator automatically converts received binary data message payload to base64 String. Assumes control
 * message payload is also base64 encoded String so we can compare the text data with normal plain text validation.
 * 
 * @author Christoph Deppisch
 */
public class GzipBinaryBase64MessageValidator extends BinaryBase64MessageValidator {

    @Override
    public void validateMessage(Message receivedMessage, Message controlMessage,
                                TestContext context, ValidationContext validationContext) throws ValidationException {
        if (receivedMessage.getPayload() instanceof byte[]) {
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(receivedMessage.getPayload(InputStream.class));
                 ByteArrayOutputStream unzipped = new ByteArrayOutputStream()) {
                StreamUtils.copy(gzipInputStream, unzipped);
                receivedMessage.setPayload(unzipped.toByteArray());

                unzipped.close();
                gzipInputStream.close();
            } catch (IOException e) {
                throw new CitrusRuntimeException("Failed to validate gzipped message", e);
            }
        }

        super.validateMessage(receivedMessage, controlMessage, context, validationContext);
    }
    
    @Override
    public boolean supportsMessageType(String messageType, Message message) {
        return messageType.equalsIgnoreCase(MessageType.GZIP_BASE64.toString());
    }
}
