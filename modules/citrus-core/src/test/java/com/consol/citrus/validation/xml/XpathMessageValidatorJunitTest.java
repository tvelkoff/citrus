/*
 * Copyright 2006-2015 the original author or authors.
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

package com.consol.citrus.validation.xml;

import com.consol.citrus.Citrus;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.UnknownElementException;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XpathMessageValidatorJunitTest {

    private XpathMessageValidator validator = new XpathMessageValidator();
    
    private TestContext context = Citrus.newInstance().createTestContext();

    @Test
    public void testValidateMessageElementsWithXPathSuccessful() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();
        validationContext.setXpathExpressions(Collections.<String, Object>singletonMap("//element/sub-element", "text-value"));

        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
    }

    @Test
    public void testValidateMessageElementsWithValidationMatcherSuccessful() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();

        Map<String, Object> validationExpressions = new HashMap<>();
        validationExpressions.put("//element/@attributeA", "@startsWith('attribute-')@");
        validationExpressions.put("//element/@attributeB", "@endsWith('-value')@");
        validationExpressions.put("//element/sub-element", "@equalsIgnoreCase('TEXT-VALUE')@");

        validationContext.setXpathExpressions(validationExpressions);

        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
    }

    @Test
    public void testValidateMessageElementsWithValidationMatcherNotSuccessful() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();

        Map<String, Object> validationExpressions = new HashMap<>();
        validationExpressions.put("//element/@attributeA", "@startsWith('attribute-')@");
        validationExpressions.put("//element/@attributeB", "@endsWith('-value')@");
        validationExpressions.put("//element/sub-element", "@contains('FAIL')@");

        validationContext.setXpathExpressions(validationExpressions);

        assertThrows(ValidationException.class, () -> validator.validateMessage(message, new DefaultMessage(), context, validationContext));
    }

    @Test
    public void testValidateMessageElementsWithValidationMatcherNotSuccessfulWhenLenient() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();

        Map<String, Object> validationExpressions = new HashMap<>();
        validationExpressions.put("//element/@attributeA", "@startsWith('attribute-')@");
        validationExpressions.put("//element/@attributeB", "@endsWith('-value')@");
        validationExpressions.put("//element/sub-element", "@contains('FAIL')@");

        validationContext.setXpathExpressions(validationExpressions);

        XpathMessageValidator validator = new XpathMessageValidator(true);
        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
        assertEquals(1, context.getFailures().size());
        assertEquals("text-value", context.getFailures().get(0).getActualResult());
        assertEquals("@contains('FAIL')@", context.getFailures().get(0).getExpectedResult());
        assertEquals("//element/sub-element", context.getFailures().get(0).getXpathExpression());
    }

    @Test
    public void testValidateMessageElementsWithXPathNotSuccessful() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();
        validationContext.setXpathExpressions(Collections.<String, Object>singletonMap(
                "//element/sub-element", "false-value"));

        assertThrows(ValidationException.class, () -> validator.validateMessage(message, new DefaultMessage(), context, validationContext));
    }

    @Test
    public void testValidateMessageElementsWithXPathNotSuccessfulWhenLenient() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidator validator = new XpathMessageValidator(true);
        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();
        validationContext.setXpathExpressions(Collections.<String, Object>singletonMap(
                "//element/sub-element", "false-value"));

        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
        assertEquals(1, context.getFailures().size());
        assertEquals("text-value", context.getFailures().get(0).getActualResult());
        assertEquals("false-value", context.getFailures().get(0).getExpectedResult());
        assertEquals("//element/sub-element", context.getFailures().get(0).getXpathExpression());
    }

    @Test
    public void testValidateMessageElementsWithDotNotationSuccessful() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();
        validationContext.setXpathExpressions(Collections.<String, Object>singletonMap(
                "root.element.sub-element", "text-value"));

        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
    }

    @Test
    public void testValidateMessageElementsWithDotNotationValidationMatcherSuccessful() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();
        validationContext.setXpathExpressions(Collections.<String, Object>singletonMap(
                "root.element.sub-element", "@contains('ext-val')@"));

        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
    }

    @Test
    public void testValidateMessageElementsWithDotNotationValidationMatcherNotSuccessful() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();
        validationContext.setXpathExpressions(Collections.<String, Object>singletonMap(
                "root.element.sub-element", "@contains(false-value)@"));

        assertThrows(ValidationException.class, () -> validator.validateMessage(message, new DefaultMessage(), context, validationContext));
    }

    @Test
    public void testValidateMessageElementsWithDotNotationValidationMatcherNotSuccessfulWhenLenient() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidator validator = new XpathMessageValidator(true);
        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();
        validationContext.setXpathExpressions(Collections.<String, Object>singletonMap(
                "root.element.sub-element", "@contains(false-value)@"));

        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
        assertEquals(1, context.getFailures().size());
        assertEquals("text-value", context.getFailures().get(0).getActualResult());
        assertEquals("@contains(false-value)@", context.getFailures().get(0).getExpectedResult());
        assertEquals("root.element.sub-element", context.getFailures().get(0).getXpathExpression());
    }

    @Test
    public void testValidateMessageElementsWithDotNotationNotSuccessful() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();
        validationContext.setXpathExpressions(Collections.<String, Object>singletonMap(
                "root.element.sub-element", "false-value"));

        assertThrows(ValidationException.class, () -> validator.validateMessage(message, new DefaultMessage(), context, validationContext));
    }

    @Test
    public void testValidateMessageElementsWithDotNotationNotSuccessfulWhenLenient() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidator validator = new XpathMessageValidator(true);
        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();
        validationContext.setXpathExpressions(Collections.<String, Object>singletonMap(
                "root.element.sub-element", "false-value"));

        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
        assertEquals(1, context.getFailures().size());
        assertEquals("text-value", context.getFailures().get(0).getActualResult());
        assertEquals("false-value", context.getFailures().get(0).getExpectedResult());
        assertEquals("root.element.sub-element", context.getFailures().get(0).getXpathExpression());
    }

    @Test
    public void testValidateMessageElementsWithDotNotationNotFound() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();
        validationContext.setXpathExpressions(Collections.<String, Object>singletonMap(
                "root.foo.bar", "not-found"));

        assertThrows(UnknownElementException.class, () -> validator.validateMessage(message, new DefaultMessage(), context, validationContext));
    }

    @Test
    public void testValidateMessageElementsWithDotNotationNotFoundWhenLenient() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidator validator = new XpathMessageValidator(true);
        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();
        validationContext.setXpathExpressions(Collections.<String, Object>singletonMap(
                "root.foo.bar", "not-found"));

        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
        assertEquals(1, context.getFailures().size());
        assertEquals("", context.getFailures().get(0).getActualResult());
        assertEquals("not-found", context.getFailures().get(0).getExpectedResult());
        assertEquals("root.foo.bar", context.getFailures().get(0).getXpathExpression());
    }

    @Test
    public void testValidateMessageElementsWithMixedNotationsSuccessful() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();
        //mix of xpath and dot-notation
        Map<String, Object> validationExpressions = new HashMap<>();
        validationExpressions.put("//element/sub-element", "text-value");
        validationExpressions.put("root.element.sub-element", "text-value");
        validationContext.setXpathExpressions(validationExpressions);

        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
    }

    @Test
    public void testValidateMessageElementsWithNodeListResult() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "<sub-element attribute='B'>other-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();
        HashMap<String, Object> expressions = new HashMap<>();
        validationContext.setXpathExpressions(expressions);

        expressions.put("node-set://element/sub-element", "text-value,other-value");
        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
        expressions.put("node-set://element/sub-element", allOf(hasSize(greaterThan(1)), not(empty())));
        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
        expressions.put("node-set://element/sub-element", "[text-value, other-value]");
        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
        expressions.put("node-set://element/sub-element", "[text-value,other-value]");
        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
        expressions.put("node-set://@attribute", "[A, B]");
        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
        expressions.put("node-set://@attribute", hasSize(2));
        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
        expressions.put("node-set://@attribute", contains("A", "B"));
        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
    }

    @Test
    public void testValidateMessageElementsWithNodeListResultNoMatch() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "<sub-element attribute='B'>other-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();

        HashMap<String, Object> expressions = new HashMap<>();
        expressions.put("node-set://element/other-element", "");
        expressions.put("boolean://element/other-element", "false");
        expressions.put("boolean://element/sub-element", "true");
        validationContext.setXpathExpressions(expressions);

        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
    }

    @Test
    public void testValidateMessageElementsWithNodeListCount() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "<sub-element attribute='B'>text-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();
        HashMap<String, Object> expressions = new HashMap<>();
        expressions.put("number:count(//element/sub-element[.='text-value'])", "2.0");
        expressions.put("integer:count(//element/sub-element[.='text-value'])", "2");
        expressions.put("number:count(//element/sub-element)", greaterThan(1.0));
        expressions.put("integer:count(//element/sub-element)", greaterThan(1));
        validationContext.setXpathExpressions(expressions);

        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
    }
    
    @Test
    public void validateMessage_emptyXpathExpressions() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "<sub-element attribute='B'>text-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();
        HashMap<String, Object> expressions = new HashMap<>();
        validationContext.setXpathExpressions(expressions);
        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
    }

    @Test
    public void validateMessage_emptyReceiveMessagePayload() {
        Message message = new DefaultMessage("");

        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();
        HashMap<String, Object> expressions = new HashMap<>();
        expressions.put("number:count(//element/sub-element[.='text-value'])", "2.0");
        expressions.put("integer:count(//element/sub-element[.='text-value'])", "2");
        expressions.put("number:count(//element/sub-element)", greaterThan(1.0));
        expressions.put("integer:count(//element/sub-element)", greaterThan(1));
        validationContext.setXpathExpressions(expressions);
        assertThrows(ValidationException.class, () -> validator.validateMessage(message, new DefaultMessage(), context, validationContext));
    }

    @Test
    public void validateMessage_nullReceiveMessagePayload() {
        Message message = new DefaultMessage();

        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();
        HashMap<String, Object> expressions = new HashMap<>();
        expressions.put("number:count(//element/sub-element[.='text-value'])", "2.0");
        expressions.put("integer:count(//element/sub-element[.='text-value'])", "2");
        expressions.put("number:count(//element/sub-element)", greaterThan(1.0));
        expressions.put("integer:count(//element/sub-element)", greaterThan(1));
        validationContext.setXpathExpressions(expressions);
        assertThrows(ValidationException.class, () -> validator.validateMessage(message, new DefaultMessage(), context, validationContext));
    }

    @Test
    public void validateMessage_ignoredElement() {
        Message message = new DefaultMessage("<root>"
                + "<element attributeA='attribute-value' attributeB='attribute-value'>"
                + "<sub-element attribute='A'>text-value</sub-element>"
                + "<sub-element attribute='B'>text-value</sub-element>"
                + "</element>"
                + "</root>");

        XpathMessageValidationContext validationContext = new XpathMessageValidationContext();
        Set<String> ignoreExpressions = new HashSet<>();
        ignoreExpressions.add("//element/sub-element");
        validationContext.setIgnoreExpressions(ignoreExpressions);
        HashMap<String, Object> expressions = new HashMap<>();
        expressions.put("//element/sub-element[1]", "text-value");
        validationContext.setXpathExpressions(expressions);
        validator.validateMessage(message, new DefaultMessage(), context, validationContext);
    }

	@Test
	public void isLenient() {
		XpathMessageValidator validator = new XpathMessageValidator(true);
		assertTrue(validator.isLenient());
	}

	@Test
	public void isNotLenient() {
		XpathMessageValidator validator = new XpathMessageValidator();
		assertFalse(validator.isLenient());
	}

}