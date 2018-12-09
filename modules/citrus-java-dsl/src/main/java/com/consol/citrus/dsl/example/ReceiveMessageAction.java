package com.consol.citrus.dsl.example;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.endpoint.Endpoint;

public class ReceiveMessageAction<T extends ReceiveMessageAction<T>> extends AbstractTestAction<T> {

	@Override
	public void doExecute(TestContext context) {
		// TODO Auto-generated method stub
		
	}

	private String messageSelector;

	private Endpoint endpoint;

	private String endpointUri;
	
	private long receiveTimeout = 0L;
	
	public ReceiveMessageAction() {
	}

	public String getMessageSelector() {
		return this.messageSelector;
	}
	
	public ReceiveMessageAction<T> setMessageSelector(String messageSelector) {
		this.messageSelector = messageSelector;
		return self();
	}

	public Endpoint getEndpoint() {
		return this.endpoint;
	}
	
	public ReceiveMessageAction<T> setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
		return self();
	}

	public String getEndpointUri() {
		return this.endpointUri;
	}
	
	public ReceiveMessageAction<T> setEndpointUri(String endpointUri) {
		this.endpointUri = endpointUri;
		return self();
	}

	public long getReceiveTimeout() {
		return this.receiveTimeout;
	}
	
	public ReceiveMessageAction<T> setReceiveTimeout(long receiveTimeout) {
		this.receiveTimeout = receiveTimeout;
		return self();
	}
	
}
