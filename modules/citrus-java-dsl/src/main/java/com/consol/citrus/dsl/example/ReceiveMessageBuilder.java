package com.consol.citrus.dsl.example;

import com.consol.citrus.endpoint.Endpoint;

public class ReceiveMessageBuilder<A extends ReceiveMessageAction<A>, T extends DelegatingTestAction<A, T>, B extends ReceiveMessageBuilder<A, T, B>> extends AbstractTestActionBuilder<T, B> {
	
	public ReceiveMessageBuilder() {
		this((A)new ReceiveMessageAction<A>());
	}

	public ReceiveMessageBuilder(A action) {
		this(new DelegatingTestAction<A, T>(action));
	}
	
	public ReceiveMessageBuilder(DelegatingTestAction<A, T> action) {
		super((T)action);
	}
	
	public B endpoint(Endpoint messageEndpoint) {
		getAction().getDelegate().setEndpoint(messageEndpoint);
		return (B)self();
	}

	public B timeout(long receiveTimeout) {
		getAction().getDelegate().setReceiveTimeout(receiveTimeout);
		return (B)self();
	}
	
	public B selector(String messageSelector) {
		getAction().getDelegate().setMessageSelector(messageSelector);
		return (B)self();
	}
	
}
