package com.consol.citrus.dsl.builder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import com.consol.citrus.actions.ReceiveMessageAction;
import com.consol.citrus.dsl.actions.DelegatingTestAction;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.message.Message;
import com.consol.citrus.validation.builder.PayloadTemplateMessageBuilder;
import com.consol.citrus.validation.builder.StaticMessageContentBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReceiveMessageBuilderTest {
	
	private ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>> builder;
	
	@Mock
	private Endpoint endpoint;
	
	@Mock
	private Message message;
	
	@Test
	public void constructor() throws Exception {
		this.builder = new ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>>();
		assertNotNull(this.builder);
		assertNotNull(this.builder.getAction());
	}
	
	@Test
	public void constructor_withAction() throws Exception {
		ReceiveMessageAction action = new ReceiveMessageAction();
		this.builder = new ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>>(action);
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
		this.builder = new ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>>();
		ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>> copy = (ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>>)this.builder.endpoint(this.endpoint);
		assertTrue(copy == this.builder);
		assertEquals(this.endpoint, this.builder.getAction().getEndpoint());
	}

	@Test
	public void endpoint_fromUri() throws Exception {
		this.builder = new ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>>();
		String uri = "http://localhost:8080/foo/bar";
		ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>> copy = (ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>>)this.builder.endpoint(uri);
		assertTrue(copy == this.builder);
		assertEquals(uri, this.builder.getAction().getEndpointUri());
	}

	@Test
	public void timeout() throws Exception {
		this.builder = new ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>>();
		ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>> copy = (ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>>)this.builder.timeout(1000L);
		assertTrue(copy == this.builder);
		assertEquals(1000L, this.builder.getAction().getReceiveTimeout());
	}

	@Test
	public void message() throws Exception {
		this.builder = new ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>>();
		ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>> copy = (ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>>)this.builder.message(this.message);
		assertTrue(copy == this.builder);
		assertNotNull(this.builder.getAction().getMessageBuilder());
	}

	@Test
	public void name() throws Exception {
		this.builder = new ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>>();
		ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>> copy = (ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>>)this.builder.name("foo");
		assertTrue(copy == this.builder);
		assertEquals("foo", this.builder.getMessageContentBuilder().getMessageName());
	}

	@Test
	public void payload_asString() throws Exception {
		this.builder = new ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>>();
		ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>> copy = (ReceiveMessageBuilder<ReceiveMessageAction, ReceiveMessageBuilder<ReceiveMessageAction, ?>>)this.builder.payload("payload");
		assertTrue(copy == this.builder);
		assertEquals("payload", ((PayloadTemplateMessageBuilder)this.builder.getMessageContentBuilder()).getPayloadData());
	}

}
