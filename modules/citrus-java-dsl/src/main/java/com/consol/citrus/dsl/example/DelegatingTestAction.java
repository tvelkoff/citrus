package com.consol.citrus.dsl.example;

import com.consol.citrus.TestActor;
import com.consol.citrus.context.TestContext;

public class DelegatingTestAction<A extends AbstractTestAction<A>, T extends DelegatingTestAction<A, T>> extends AbstractTestAction<T> {

	private A delegate;

	public DelegatingTestAction() {
		super();
	}

	public DelegatingTestAction(A delegate) {
		this.delegate = delegate;
	}

	@Override
	public void doExecute(TestContext context) {
		if (this.delegate != null) {
			getDelegate().execute(context);
		}
	}

	@Override
	public boolean isDisabled(TestContext context) {
		return getDelegate().isDisabled(context);
	}

	@Override
	public TestActor getActor() {
		return getDelegate().getActor();
	}

	@Override
	public T setActor(TestActor actor) {
		return (T)getDelegate().setActor(actor);
	}

	@Override
	public String getDescription() {
		return getDelegate().getDescription();
	}

	@Override
	public T setDescription(String description) {
		return (T)getDelegate().setDescription(description);
	}

	@Override
	public String getName() {
		return getDelegate().getName();
	}

	@Override
	public T setName(String name) {
		return (T)getDelegate().setName(name);
	}

	public void setDelegate(A delegate) {
		this.delegate = delegate;
	}

	public A getDelegate() {
		return this.delegate;
	}

}
