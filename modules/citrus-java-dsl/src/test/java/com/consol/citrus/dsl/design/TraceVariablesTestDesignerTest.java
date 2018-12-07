/*
 * Copyright 2006-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.dsl.design;

import com.consol.citrus.TestCase;
import com.consol.citrus.testng.AbstractTestNGUnitTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.consol.citrus.actions.TraceVariablesAction;

public class TraceVariablesTestDesignerTest extends AbstractTestNGUnitTest {

	@Test
	public void testTraceVariablesBuilder() {
		MockTestDesigner builder = new MockTestDesigner(applicationContext, context) {
			@Override
			public void configure() {
				traceVariables();
				traceVariables("variable1", "variable2");
			}
		};

		builder.configure();

		TestCase test = builder.getTestCase();
		Assert.assertEquals(test.getActionCount(), 2);
		Assert.assertEquals(test.getActions().get(0).getClass(), TraceVariablesAction.class);
		Assert.assertEquals(test.getActions().get(1).getClass(), TraceVariablesAction.class);
		
		TraceVariablesAction action = (TraceVariablesAction)test.getActions().get(0);
		Assert.assertEquals(action.getName(), "trace");
		Assert.assertNull(action.getVariableNames());
		
		action = (TraceVariablesAction)test.getActions().get(1);
        Assert.assertEquals(action.getName(), "trace");
        Assert.assertNotNull(action.getVariableNames());
        Assert.assertEquals(action.getVariableNames().toString(), "[variable1, variable2]");
	}
}
