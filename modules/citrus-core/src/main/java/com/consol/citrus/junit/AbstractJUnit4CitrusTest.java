/*
 * Copyright 2006-2010 the original author or authors.
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

package com.consol.citrus.junit;

import com.consol.citrus.Citrus;
import com.consol.citrus.TestCase;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.common.TestLoader;
import com.consol.citrus.common.XmlTestLoader;
import com.consol.citrus.config.CitrusSpringConfig;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;

/**
 * Abstract base test implementation for test cases that rather use JUnit testing framework. Class also provides 
 * test listener support and loads the root application context files for Citrus.
 * 
 * @author Christoph Deppisch
 */
@RunWith(CitrusJUnit4Runner.class)
@ContextConfiguration(classes = CitrusSpringConfig.class)
public abstract class AbstractJUnit4CitrusTest extends AbstractJUnit4SpringContextTests {

    /** Logger */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /** Citrus instance */
    protected Citrus citrus;

    /**
     * Reads Citrus test annotation from framework method and executes test case.
     * @param frameworkMethod
     */
    protected void run(CitrusJUnit4Runner.CitrusFrameworkMethod frameworkMethod) {
        if (citrus == null) {
            citrus = Citrus.newInstance(applicationContext);
        }

        TestContext ctx = prepareTestContext(citrus.createTestContext());
        TestLoader testLoader = createTestLoader(frameworkMethod.getTestName(), frameworkMethod.getPackageName());
        TestCase testCase = testLoader.load();

        citrus.run(testCase, ctx);
    }

    /**
     * Resolves method arguments supporting TestNG data provider parameters as well as
     * {@link CitrusResource} annotated methods.
     *
     * @param frameworkMethod
     * @param testCase
     * @param context
     * @return
     */
    protected Object[] resolveParameter(CitrusJUnit4Runner.CitrusFrameworkMethod frameworkMethod, TestCase testCase, TestContext context) {
        Object[] values = new Object[frameworkMethod.getMethod().getParameterTypes().length];
        Class<?>[] parameterTypes = frameworkMethod.getMethod().getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            final Annotation[] parameterAnnotations = frameworkMethod.getMethod().getParameterAnnotations()[i];
            Class<?> parameterType = parameterTypes[i];
            for (Annotation annotation : parameterAnnotations) {
                if (annotation instanceof CitrusResource) {
                    values[i] = resolveAnnotatedResource(frameworkMethod, parameterType, context);
                }
            }
        }

        return values;
    }

    /**
     * Resolves value for annotated method parameter.
     *
     * @param frameworkMethod
     * @param parameterType
     * @return
     */
    protected Object resolveAnnotatedResource(CitrusJUnit4Runner.CitrusFrameworkMethod frameworkMethod, Class<?> parameterType, TestContext context) {
        if (TestContext.class.isAssignableFrom(parameterType)) {
            return context;
        } else {
            throw new CitrusRuntimeException("Not able to provide a Citrus resource injection for type " + parameterType);
        }
    }

    /**
     * Execute the test case.
     */
    protected void executeTest() {
        run(new CitrusJUnit4Runner.CitrusFrameworkMethod(ReflectionUtils.findMethod(this.getClass(), "executeTest"),
                this.getClass().getSimpleName(), this.getClass().getPackage().getName()));
    }

    /**
     * Prepares the test context.
     *
     * Provides a hook for test context modifications before the test gets executed.
     *
     * @param testContext the test context.
     * @return the (prepared) test context.
     */
    protected TestContext prepareTestContext(final TestContext testContext) {
        return testContext;
    }

    /**
     * Creates new test loader which has TestNG test annotations set for test execution. Only
     * suitable for tests that get created at runtime through factory method. Subclasses
     * may overwrite this in order to provide custom test loader with custom test annotations set.
     * @param testName
     * @param packageName
     * @return
     */
    protected TestLoader createTestLoader(String testName, String packageName) {
        return new XmlTestLoader(getClass(), testName, packageName, applicationContext);
    }

    /**
     * Constructs the test case to execute.
     * @return
     */
    protected TestCase getTestCase() {
        return createTestLoader(this.getClass().getSimpleName(), this.getClass().getPackage().getName()).load();
    }
}
