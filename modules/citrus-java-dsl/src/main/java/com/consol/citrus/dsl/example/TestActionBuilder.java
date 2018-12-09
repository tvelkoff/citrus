package com.consol.citrus.dsl.example;

public interface TestActionBuilder<T extends TestAction<T>> {
	
	T build();

}
