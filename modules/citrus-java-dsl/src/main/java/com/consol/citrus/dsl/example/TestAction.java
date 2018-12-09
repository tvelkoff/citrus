package com.consol.citrus.dsl.example;

import com.consol.citrus.TestActor;
import com.consol.citrus.context.TestContext;

public interface TestAction<T extends TestAction<T>> {

	void execute(TestContext context);
	boolean isDisabled(TestContext context);

	String getName();
	T setName(String name);
	String getDescription();
	T setDescription(String description);
	TestActor getActor();
	T setActor(TestActor actor);

}
