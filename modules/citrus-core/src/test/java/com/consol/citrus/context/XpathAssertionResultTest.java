/*
 * Copyright 2006-2019 the original author or authors.
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

package com.consol.citrus.context;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.consol.citrus.context.XpathAssertionResult;

public class XpathAssertionResultTest {

	@Test
	void constructor() {
		//GIVEN
		final String xpathExpression = "//item";
		final String expectedResult = "foo";
		final String actualResult = "bar";

		//WHEN
		XpathAssertionResult result = new XpathAssertionResult(xpathExpression, expectedResult, actualResult);

		//THEN
		assertEquals(xpathExpression, result.getXpathExpression());
		assertEquals(expectedResult, result.getExpectedResult());
		assertEquals(actualResult, result.getActualResult());
		assertEquals("XPath assertion failure - //item : expected = foo, actual = bar", result.toString());
	}

}
