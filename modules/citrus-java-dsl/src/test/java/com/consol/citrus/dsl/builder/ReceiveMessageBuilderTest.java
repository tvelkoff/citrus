package com.consol.citrus.dsl.builder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import com.consol.citrus.actions.ReceiveMessageAction;
import com.consol.citrus.dsl.actions.DelegatingTestAction;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.message.Message;
import com.consol.citrus.validation.builder.PayloadTemplateMessageBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.xml.transform.StringResult;

@ExtendWith(MockitoExtension.class)
public class ReceiveMessageBuilderTest {
	
	private ReceiveMessageBuilder builder;
	
	@Mock
	private Endpoint endpoint;
	
	@Mock
	private Message message;
	
	@Mock
	private Resource resource;
	
	@Test
	public void constructor() throws Exception {
		this.builder = new ReceiveMessageBuilder();
		assertNotNull(this.builder);
		assertNotNull(this.builder.getAction());
	}
	
	@Test
	public void constructor_withAction() throws Exception {
		ReceiveMessageAction action = new ReceiveMessageAction();
		this.builder = new ReceiveMessageBuilder(action);
		assertNotNull(this.builder);
		assertEquals(action, this.builder.getAction());
	}

	@Test
	public void constructor_withDelegatingTestAction() throws Exception {
		DelegatingTestAction<ReceiveMessageAction> action = new DelegatingTestAction<ReceiveMessageAction>(new ReceiveMessageAction());
		this.builder = new ReceiveMessageBuilder(action);
		assertNotNull(this.builder);
		assertEquals(action.getDelegate(), this.builder.getAction());
	}
	
	@Test
	public void endpoint_fromEndpoint() throws Exception {
		this.builder = new ReceiveMessageBuilder();
		ReceiveMessageBuilder copy = (ReceiveMessageBuilder)this.builder.endpoint(this.endpoint);
		assertTrue(copy == this.builder);
		assertEquals(this.endpoint, this.builder.getAction().getEndpoint());
	}

	@Test
	public void endpoint_fromUri() throws Exception {
		this.builder = new ReceiveMessageBuilder();
		String uri = "http://localhost:8080/foo/bar";
		ReceiveMessageBuilder copy = (ReceiveMessageBuilder)this.builder.endpoint(uri);
		assertTrue(copy == this.builder);
		assertEquals(uri, this.builder.getAction().getEndpointUri());
	}

	@Test
	public void timeout() throws Exception {
		this.builder = new ReceiveMessageBuilder();
		ReceiveMessageBuilder copy = (ReceiveMessageBuilder)this.builder.timeout(1000L);
		assertTrue(copy == this.builder);
		assertEquals(1000L, this.builder.getAction().getReceiveTimeout());
	}

	@Test
	public void message() throws Exception {
		this.builder = new ReceiveMessageBuilder();
		ReceiveMessageBuilder copy = (ReceiveMessageBuilder)this.builder.message(this.message);
		assertTrue(copy == this.builder);
		assertNotNull(this.builder.getAction().getMessageBuilder());
	}

	@Test
	public void name() throws Exception {
		this.builder = new ReceiveMessageBuilder();
		ReceiveMessageBuilder copy = (ReceiveMessageBuilder)this.builder.name("foo");
		assertTrue(copy == this.builder);
		assertEquals("foo", this.builder.getMessageContentBuilder().getMessageName());
	}

	@Test
	public void payload_asString() throws Exception {
		this.builder = new ReceiveMessageBuilder();
		ReceiveMessageBuilder copy = (ReceiveMessageBuilder)this.builder.payload("payload");
		assertTrue(copy == this.builder);
		assertEquals("payload", ((PayloadTemplateMessageBuilder)this.builder.getMessageContentBuilder()).getPayloadData());
	}

	@Test
	public void payload_asResource() throws Exception {
		this.builder = new ReceiveMessageBuilder();
		ReceiveMessageBuilder copy = (ReceiveMessageBuilder)this.builder.payload(this.resource);
		assertTrue(copy == this.builder);
		assertNotNull(((PayloadTemplateMessageBuilder)this.builder.getMessageContentBuilder()).getPayloadData());
	}

	@Test
	public void payload_asResourceWithCharset() throws Exception {
		this.builder = new ReceiveMessageBuilder();
		ReceiveMessageBuilder copy = (ReceiveMessageBuilder)this.builder.payload(this.resource, Charset.defaultCharset());
		assertTrue(copy == this.builder);
		assertNotNull(((PayloadTemplateMessageBuilder)this.builder.getMessageContentBuilder()).getPayloadData());
	}

	@Test
	public void payload_asObjectWithMarshaller() throws Exception {
		this.builder = new ReceiveMessageBuilder();
		Object payload = "<hello/>";
		Marshaller marshaller = mock(Marshaller.class);
		ReceiveMessageBuilder copy = (ReceiveMessageBuilder)this.builder.payload(payload, marshaller);
		assertTrue(copy == this.builder);
		assertNotNull(((PayloadTemplateMessageBuilder)this.builder.getMessageContentBuilder()).getPayloadData());
	}

	@Test
	public void payload_asObjectWithMapper() throws Exception {
		this.builder = new ReceiveMessageBuilder();
		Object payload = "{hello}";
		ObjectMapper mapper = mock(ObjectMapper.class);
		ObjectWriter writer = mock(ObjectWriter.class);
		when(mapper.writer()).thenReturn(writer);
		when(writer.writeValueAsString(payload)).thenReturn("hello");
		ReceiveMessageBuilder copy = (ReceiveMessageBuilder)this.builder.payload(payload, mapper);
		assertTrue(copy == this.builder);
		assertNotNull(((PayloadTemplateMessageBuilder)this.builder.getMessageContentBuilder()).getPayloadData());
		assertEquals("hello", ((PayloadTemplateMessageBuilder)this.builder.getMessageContentBuilder()).getPayloadData());
	}

	@Test
	public void payload_asObjectWithString_toObjectMarshaller() throws Exception {
		Object payload = "{hello}";
		String mapperName = "mapper";
		this.builder = new ReceiveMessageBuilder();
		ApplicationContext mockApplicationContext = mock(ApplicationContext.class);
		ReflectionTestUtils.setField(this.builder, "applicationContext", mockApplicationContext);
		when(mockApplicationContext.containsBean(mapperName)).thenReturn(true);
		Marshaller marshaller = mock(Marshaller.class);
		when(mockApplicationContext.getBean(mapperName)).thenReturn(marshaller);
		lenient().doNothing().when(marshaller).marshal(payload, new StringResult());
		ReceiveMessageBuilder copy = (ReceiveMessageBuilder)this.builder.payload(payload, mapperName);
		assertTrue(copy == this.builder);
		assertNotNull(((PayloadTemplateMessageBuilder)this.builder.getMessageContentBuilder()).getPayloadData());
		ReflectionTestUtils.setField(this.builder, "applicationContext", null);
	}

	@Test
	public void payload_asObjectWithString_toObjectMapper() throws Exception {
		Object payload = "{hello}";
		String mapperName = "mapper";
		this.builder = new ReceiveMessageBuilder();
		ApplicationContext mockApplicationContext = mock(ApplicationContext.class);
		ReflectionTestUtils.setField(this.builder, "applicationContext", mockApplicationContext);
		when(mockApplicationContext.containsBean(mapperName)).thenReturn(true);
		ObjectMapper mapper = mock(ObjectMapper.class);
		ObjectWriter writer = mock(ObjectWriter.class);
		when(mockApplicationContext.getBean(mapperName)).thenReturn(mapper);
		when(mapper.writer()).thenReturn(writer);
		when(writer.writeValueAsString(payload)).thenReturn("hello");
		ReceiveMessageBuilder copy = (ReceiveMessageBuilder)this.builder.payload(payload, mapperName);
		assertTrue(copy == this.builder);
		assertNotNull(((PayloadTemplateMessageBuilder)this.builder.getMessageContentBuilder()).getPayloadData());
		assertEquals("hello", ((PayloadTemplateMessageBuilder)this.builder.getMessageContentBuilder()).getPayloadData());
		ReflectionTestUtils.setField(this.builder, "applicationContext", null);
	}

	@Test
	public void payloadModel_withMarshaller() throws Exception {
		this.builder = new ReceiveMessageBuilder();
		Object payload = "<hello/>";
		Marshaller marshaller = mock(Marshaller.class);
		ApplicationContext mockApplicationContext = mock(ApplicationContext.class);
		ReflectionTestUtils.setField(this.builder, "applicationContext", mockApplicationContext);
		Map<String, Marshaller> map = new HashMap<>();
		map.put("marshaller", marshaller);
		when(mockApplicationContext.getBeansOfType(Marshaller.class)).thenReturn(map);
		when(mockApplicationContext.getBean(Marshaller.class)).thenReturn(marshaller);
		ReceiveMessageBuilder copy = (ReceiveMessageBuilder)this.builder.payloadModel(payload);
		assertTrue(copy == this.builder);
		assertNotNull(((PayloadTemplateMessageBuilder)this.builder.getMessageContentBuilder()).getPayloadData());
		ReflectionTestUtils.setField(this.builder, "applicationContext", null);
	}

	@Test
	public void payloadModel_withObjectMapper() throws Exception {
		this.builder = new ReceiveMessageBuilder();
		Object payload = "<hello/>";
		ObjectMapper mapper = mock(ObjectMapper.class);
		ApplicationContext mockApplicationContext = mock(ApplicationContext.class);
		ReflectionTestUtils.setField(this.builder, "applicationContext", mockApplicationContext);
		Map<String, ObjectMapper> map = new HashMap<>();
		map.put("mapper", mapper);
		when(mockApplicationContext.getBeansOfType(Marshaller.class)).thenReturn(new HashMap<String, Marshaller>());
		when(mockApplicationContext.getBeansOfType(ObjectMapper.class)).thenReturn(map);
		when(mockApplicationContext.getBean(ObjectMapper.class)).thenReturn(mapper);
		ReceiveMessageBuilder copy = (ReceiveMessageBuilder)this.builder.payloadModel(payload);
		assertTrue(copy == this.builder);
		assertNotNull(((PayloadTemplateMessageBuilder)this.builder.getMessageContentBuilder()).getPayloadData());
		ReflectionTestUtils.setField(this.builder, "applicationContext", null);
	}

}
