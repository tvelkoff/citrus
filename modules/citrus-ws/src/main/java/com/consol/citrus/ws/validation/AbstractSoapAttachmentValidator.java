/*
 * Copyright 2006-2010 the original author or authors.
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

package com.consol.citrus.ws.validation;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.Message;
import com.consol.citrus.ws.SoapAttachment;
import com.consol.citrus.ws.message.CitrusSoapMessageHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * Abstract SOAP attachment validator tries to find attachment within received message and compares
 * its attachment contentId, contentType and content body to a control attachment definition. 
 * 
 * Validator will create a {@link SoapAttachment} and automatically handle contentId and 
 * contentType validation. Content body validation is delegated to subclasses.
 * 
 * @author Christoph Deppisch
 */
public abstract class AbstractSoapAttachmentValidator implements SoapAttachmentValidator {
    /**
     * Logger
     */
    private static Logger log = LoggerFactory.getLogger(AbstractSoapAttachmentValidator.class);
    
    /**
     * Validate contentId and contentType of attachment to meet control attachment.
     * 
     * @param receivedMessage
     * @param controlAttachment
     */
    public void validateAttachment(Message receivedMessage, SoapAttachment controlAttachment) throws IOException {
        log.info("Validating SOAP attachments ...");
        
        if (receivedMessage.getHeader(CitrusSoapMessageHeaders.CONTENT_ID) != null) {
            if (log.isDebugEnabled()) {
                log.debug("Found attachment with contentId '" + receivedMessage.getHeader(CitrusSoapMessageHeaders.CONTENT_ID) + "'");
            }
            
            SoapAttachment attachment = new SoapAttachment();
            
            attachment.setContentId(receivedMessage.getHeader(CitrusSoapMessageHeaders.CONTENT_ID).toString());
            
            if (receivedMessage.getHeader(CitrusSoapMessageHeaders.CONTENT_TYPE) != null) {
                attachment.setContentType(receivedMessage.getHeader(CitrusSoapMessageHeaders.CONTENT_TYPE).toString());
            }
            
            if (receivedMessage.getHeader(CitrusSoapMessageHeaders.CONTENT) != null) {
                Object contentObject = receivedMessage.getHeader(CitrusSoapMessageHeaders.CONTENT);
                
                if (contentObject instanceof byte[]) {
                    String content = new String((byte[])contentObject, controlAttachment.getCharsetName());
                    
                    if (content.contains("<?xml")) {
                        //strip off possible leading prolog characters in xml content
                        attachment.setContent(content.substring(content.indexOf('<')));
                    } else {
                        attachment.setContent(content);
                    }
                } else if (contentObject instanceof String) {
                    attachment.setContent(contentObject.toString());
                } else {
                    throw new IllegalArgumentException("Unsupported attachment content object (" + contentObject.getClass() + ")." +
                    		" Either byte[] or java.lang.String are supported.");
                }
            }
            
            validateAttachmentContentId(attachment, controlAttachment);
            validateAttachmentContentType(attachment, controlAttachment);
            validateAttachmentContent(attachment, controlAttachment);
            
            log.info("Validation of SOAP attachment finished successfully: All values OK");
        } else {
            throw new CitrusRuntimeException("Missing SOAP attachment with contentId '" + controlAttachment.getContentId() + "'");
        }
    }
    
    /**
     * Validating SOAP attachment content id.
     * @param receivedAttachment
     * @param controlAttachment
     */
    protected void validateAttachmentContentId(SoapAttachment receivedAttachment, SoapAttachment controlAttachment) {
        //in case contentId was not set in test case, skip validation 
        if (!StringUtils.hasText(controlAttachment.getContentId())) { return; }
        
        if (receivedAttachment.getContentId() != null) {
            Assert.isTrue(controlAttachment.getContentId() != null, 
                    buildValidationErrorMessage("Values not equal for attachment contentId", 
                            null, receivedAttachment.getContentId()));

            Assert.isTrue(receivedAttachment.getContentId().equals(controlAttachment.getContentId()),
                    buildValidationErrorMessage("Values not equal for attachment contentId", 
                            controlAttachment.getContentId(), receivedAttachment.getContentId()));
        } else {
            Assert.isTrue(controlAttachment.getContentId() == null || controlAttachment.getContentId().length() == 0, 
                    buildValidationErrorMessage("Values not equal for attachment contentId", 
                            controlAttachment.getContentId(), null));
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Validating attachment contentId: " + receivedAttachment.getContentId() + 
                    "='" + controlAttachment.getContentId() + "': OK.");
        }
    }
    
    /**
     * Validating SOAP attachment content type.
     * @param receivedAttachment
     * @param controlAttachment
     */
    protected void validateAttachmentContentType(SoapAttachment receivedAttachment, SoapAttachment controlAttachment) {
        //in case contentType was not set in test case, skip validation
        if (!StringUtils.hasText(controlAttachment.getContentType())) { return; }
        
        if (receivedAttachment.getContentType() != null) {
            Assert.isTrue(controlAttachment.getContentType() != null, 
                    buildValidationErrorMessage("Values not equal for attachment contentType", 
                            null, receivedAttachment.getContentType()));

            Assert.isTrue(receivedAttachment.getContentType().equals(controlAttachment.getContentType()),
                    buildValidationErrorMessage("Values not equal for attachment contentType", 
                            controlAttachment.getContentType(), receivedAttachment.getContentType()));
        } else {
            Assert.isTrue(controlAttachment.getContentType() == null || controlAttachment.getContentType().length() == 0, 
                    buildValidationErrorMessage("Values not equal for attachment contentType", 
                            controlAttachment.getContentType(), null));
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Validating attachment contentType: " + receivedAttachment.getContentType() + 
                    "='" + controlAttachment.getContentType() + "': OK.");
        }
    }
    
    /**
     * Constructs proper error message with expected value and actual value.
     * @param message the base error message.
     * @param expectedValue the expected value.
     * @param actualValue the actual value.
     * @return
     */
    private String buildValidationErrorMessage(String message, Object expectedValue, Object actualValue) {
        return message + ", expected '" + expectedValue + "' but was '" + actualValue + "'";
    }

    /**
     * Delegate content body validation to subclasses.
     * @param receivedAttachment
     * @param controlAttachment
     */
    protected abstract void validateAttachmentContent(SoapAttachment receivedAttachment, SoapAttachment controlAttachment);
}
