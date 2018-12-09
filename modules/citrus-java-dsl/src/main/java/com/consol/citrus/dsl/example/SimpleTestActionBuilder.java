package com.consol.citrus.dsl.example;

public class SimpleTestActionBuilder<T extends AbstractTestAction<T>, B extends AbstractTestActionBuilder<T, B>> extends AbstractTestActionBuilder<T, B> {

	public SimpleTestActionBuilder(T action) {
		super(action);
	}
	
}
