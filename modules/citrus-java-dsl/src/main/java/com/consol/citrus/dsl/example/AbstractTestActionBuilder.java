package com.consol.citrus.dsl.example;

import com.consol.citrus.TestActor;
import com.consol.citrus.context.TestContext;

public abstract class AbstractTestActionBuilder<T extends AbstractTestAction<T>, B extends AbstractTestActionBuilder<T, B>> implements TestAction<T>, TestActionBuilder<T> {

	protected T action;
	
	public AbstractTestActionBuilder(T action) {
		this.action = action;
	}
	
	public AbstractTestActionBuilder<T, B> description(String description) {
		this.action.setDescription(description);
		return this;
	}
	
	public AbstractTestActionBuilder<T, B> name(String name) {
		this.action.setName(name);
		return this;
	}
	
	public AbstractTestActionBuilder<T, B> actor(TestActor actor) {
		this.action.setActor(actor);
		return this;
	}
	
	public T build() {
		return this.action;
	}
	
	public AbstractTestActionBuilder<T, B> self() {
		return this;
	}
	
	@Override
	public void execute(TestContext context) {
		throw new IllegalStateException("Test action builder must not be executed");
	}

	@Override
	public boolean isDisabled(TestContext context) {
		return false;
	}
	
	@Override
	public String getName() {
		return this.action.getName();
	}

	@Override
	public T setName(String name) {
		return this.action.setName(name);
	}
	
	@Override
	public String getDescription() {
		return this.action.getDescription();
	}

	@Override
	public T setDescription(String description) {
		return this.action.setDescription(description);
	}
	
	@Override
	public TestActor getActor() {
		return this.action.getActor();
	}

	@Override
	public T setActor(TestActor actor) {
		return this.action.setActor(actor);
	}
	
	protected T getAction() {
		return this.action;
	}
	
}
