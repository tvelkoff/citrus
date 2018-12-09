package com.consol.citrus.dsl.example;

import com.consol.citrus.TestActor;
import com.consol.citrus.context.TestContext;

public abstract class AbstractTestAction<T extends AbstractTestAction<T>> implements TestAction<T> {

	private TestActor actor;
	protected String description;
	private String name = this.getClass().getSimpleName();
	
	public abstract void doExecute(TestContext context);

	@Override
	public void execute(TestContext context) {
		doExecute(context);
	}

	@Override
	public boolean isDisabled(TestContext context) {
		if (this.actor != null) {
				return this.actor.isDisabled();
		} else {
				return false;
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public T setName(String name) {
		this.name = name;
		return self();
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public T setDescription(String description) {
		this.description = description;
		return self();
	}

	@Override
	public TestActor getActor() {
		return this.actor;
	}

	@Override
	public T setActor(TestActor actor) {
		this.actor = actor;
		return self();
	}
	
	protected T self() {
		return (T)this;
	}

}
