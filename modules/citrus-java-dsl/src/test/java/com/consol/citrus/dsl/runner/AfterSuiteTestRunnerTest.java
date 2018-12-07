/*
 * Copyright 2006-2016 the original author or authors.
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

package com.consol.citrus.dsl.runner;

import com.consol.citrus.testng.AbstractTestNGUnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 * @since 2.6
 */
public class AfterSuiteTestRunnerTest extends AbstractTestNGUnitTest {

    @Autowired
    private AfterSuiteTestRunner afterSuiteTestRunner;

    @Test
    public void testAfterSuiteBuilder() {
        Assert.assertEquals(afterSuiteTestRunner.getExecutionCount(), 0);
        afterSuiteTestRunner.execute(context);
        Assert.assertEquals(afterSuiteTestRunner.getExecutionCount(), 1);
    }

}
