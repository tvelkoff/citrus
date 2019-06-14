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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.consol.citrus.Citrus;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.exceptions.UnknownElementException;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;

public class XpathPayloadVariableExtractorJunitTest {
	
	private XpathPayloadVariableExtractor extractor = new XpathPayloadVariableExtractor();
	
    private TestContext context = Citrus.newInstance().createTestContext();

    private static String sampleMessage =
			"<ns1:person xmlns:ns1=\"http://sample.org/p\" xmlns:ns2=\"http://sample.org/q\" status=\"married\">" +
				"<ns2:name>foo</ns2:name>" +
				"<ns2:age>23</ns2:age>" +
			"</ns1:person>";
		
	private static String sampleMessageWithMultiples =
			"<ns1:person xmlns:ns1=\"http://sample.org/p\" xmlns:ns2=\"http://sample.org/q\" status=\"married\">" +
				"<ns2:name>foo</ns2:name>" +
				"<ns2:name>bar</ns2:name>" +
				"<ns2:age>23</ns2:age>" +
				"<ns2:age>24</ns2:age>" +
			"</ns1:person>";
		
	private static Map<String, String> sampleNamespaces = new HashMap<>();
	
	static {
		sampleNamespaces.put("ns1", "http://sample.org/p");
		sampleNamespaces.put("ns2", "http://sample.org/q");
	}

	@Test
	public void expressions() {
		Map<String, String> expressions = new HashMap<>();
		extractor.setXpathExpressions(expressions);
		assertEquals(expressions, extractor.getXpathExpressions());
	}
	
	@Test
	public void namespaces() {
		Map<String, String> namespaces = new HashMap<>();
		extractor.setNamespaces(namespaces);
		assertEquals(namespaces, extractor.getNamespaces());
	}
	
	@Test
	public void extractVariables_whenExpressionIsEmpty() {
		assertFalse(context.hasVariables());
		Message message = new DefaultMessage();
		extractor.extractVariables(message, context);
		assertFalse(context.hasVariables());
	}
	
	@Test
	public void extractVariables_whenSlashExpressionYieldsSingle() {
		Message message = new DefaultMessage(sampleMessage);
		Map<String, String> slashExpressions = new HashMap<>();
		slashExpressions.put("/ns1:person/ns2:name", "name");
		extractor.setNamespaces(sampleNamespaces);
		extractor.setXpathExpressions(slashExpressions);
		extractor.extractVariables(message, context);
		assertEquals("foo", context.getVariable("name"));
	}

	@Test
	public void extractVariables_whenSlashExpressionsYieldSingle() {
		Message message = new DefaultMessage(sampleMessage);
		Map<String, String> slashExpressions = new HashMap<>();
		slashExpressions.put("/ns1:person/ns2:name", "name");
		slashExpressions.put("/ns1:person/ns2:age", "age");
		extractor.setNamespaces(sampleNamespaces);
		extractor.setXpathExpressions(slashExpressions);
		extractor.extractVariables(message, context);
		assertEquals("foo", context.getVariable("name"));
		assertEquals("23", context.getVariable("age"));
	}

	@Test
	public void extractVariables_whenDotExpressionYieldsSingle() {
		Message message = new DefaultMessage(sampleMessage);
		Map<String, String> dotExpressions = new HashMap<>();
		dotExpressions.put("person.name", "name");
		extractor.setNamespaces(sampleNamespaces);
		extractor.setXpathExpressions(dotExpressions);
		extractor.extractVariables(message, context);
		assertEquals("foo", context.getVariable("name"));
	}

	@Test
	public void extractVariables_whenDotExpressionsYieldSingle() {
		Message message = new DefaultMessage(sampleMessage);
		Map<String, String> dotExpressions = new HashMap<>();
		dotExpressions.put("person.name", "name");
		dotExpressions.put("person.age", "age");
		extractor.setNamespaces(sampleNamespaces);
		extractor.setXpathExpressions(dotExpressions);
		extractor.extractVariables(message, context);
		assertEquals("foo", context.getVariable("name"));
		assertEquals("23", context.getVariable("age"));
	}

	@Test
	public void extractVariables_whenSlashExpressionsYieldMultiple() {
		Message message = new DefaultMessage(sampleMessageWithMultiples);
		Map<String, String> slashExpressions = new HashMap<>();
		slashExpressions.put("node-set:/ns1:person/ns2:name", "name");
		slashExpressions.put("node-set:/ns1:person/ns2:age", "age");
		extractor.setNamespaces(sampleNamespaces);
		extractor.setXpathExpressions(slashExpressions);
		extractor.extractVariables(message, context);
		assertEquals("foo,bar", context.getVariable("name"));
		assertEquals("23,24", context.getVariable("age"));
	}

	@Test
	public void extractVariables_whenDotExpressionsYieldMultiple() {
		Message message = new DefaultMessage(sampleMessageWithMultiples);
		Map<String, String> slashExpressions = new HashMap<>();
		slashExpressions.put("person.name", "name");
		slashExpressions.put("person.age", "age");
		extractor.setNamespaces(sampleNamespaces);
		extractor.setXpathExpressions(slashExpressions);
		extractor.extractVariables(message, context);
		assertEquals("foo", context.getVariable("name"));
		assertEquals("23", context.getVariable("age"));
	}

	@Test
	public void extractVariables_whenSlashExpressionYieldsNotFound() {
		Message message = new DefaultMessage(sampleMessage);
		Map<String, String> slashExpressions = new HashMap<>();
		slashExpressions.put("/ns1:person/ns2:dob", "dob");
		extractor.setNamespaces(sampleNamespaces);
		extractor.setXpathExpressions(slashExpressions);
		assertThrows(CitrusRuntimeException.class, () -> extractor.extractVariables(message, context));
	}

	@Test
	public void extractVariables_whenDotExpressionYieldsNotFound() {
		Message message = new DefaultMessage(sampleMessage);
		Map<String, String> dotExpressions = new HashMap<>();
		dotExpressions.put("person.dob", "dob");
		extractor.setNamespaces(sampleNamespaces);
		extractor.setXpathExpressions(dotExpressions);
		assertThrows(UnknownElementException.class, () -> extractor.extractVariables(message, context));
	}
	
	@Test
	public void isLenient() {
		XpathPayloadVariableExtractor extractor = new XpathPayloadVariableExtractor(true);
		assertTrue(extractor.isLenient());
	}

	@Test
	public void isNotLenient() {
		XpathPayloadVariableExtractor extractor = new XpathPayloadVariableExtractor();
		assertFalse(extractor.isLenient());
	}

	@Test
	public void extractVariables_whenSlashExpressionYieldsNotFoundButLenient() {
		XpathPayloadVariableExtractor extractor = new XpathPayloadVariableExtractor(true);
		Message message = new DefaultMessage(sampleMessage);
		Map<String, String> slashExpressions = new HashMap<>();
		slashExpressions.put("/ns1:person/ns2:dob", "dob");
		extractor.setNamespaces(sampleNamespaces);
		extractor.setXpathExpressions(slashExpressions);
		extractor.extractVariables(message, context);
		assertEquals("", context.getVariable("dob"));
	}

	@Test
	public void extractVariables_whenDotExpressionYieldsNotFoundButLenient() {
		XpathPayloadVariableExtractor extractor = new XpathPayloadVariableExtractor(true);
		Message message = new DefaultMessage(sampleMessage);
		Map<String, String> dotExpressions = new HashMap<>();
		dotExpressions.put("person.dob", "dob");
		extractor.setNamespaces(sampleNamespaces);
		extractor.setXpathExpressions(dotExpressions);
		extractor.extractVariables(message, context);
		assertEquals("", context.getVariable("dob"));
	}
	
}
