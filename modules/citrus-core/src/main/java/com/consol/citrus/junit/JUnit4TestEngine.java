/*
 * Copyright 2006-2018 the original author or authors.
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

import com.consol.citrus.TestClass;
import com.consol.citrus.main.AbstractTestEngine;
import com.consol.citrus.main.TestRunConfiguration;
import com.consol.citrus.main.scan.ClassPathTestScanner;
import com.consol.citrus.main.scan.JarFileTestScanner;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.*;
import java.util.*;

/**
 * @author Christoph Deppisch
 * @since 2.7.4
 */
public class JUnit4TestEngine extends AbstractTestEngine {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(JUnit4TestEngine.class);

    private List<RunListener> listeners = new ArrayList<>();

    /**
     * Default constructor using run configuration.
     * @param configuration
     */
    public JUnit4TestEngine(TestRunConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void run() {
        if (!CollectionUtils.isEmpty(getConfiguration().getTestClasses())) {
            run(getConfiguration().getTestClasses());
        } else {
            List<String> packagesToRun = getConfiguration().getPackages();
            if (CollectionUtils.isEmpty(packagesToRun) && CollectionUtils.isEmpty(getConfiguration().getTestClasses())) {
                packagesToRun = Collections.singletonList("");
                log.info("Running all tests in project");
            }

            List<TestClass> classesToRun = new ArrayList<>();
            for (String packageName : packagesToRun) {
                if (StringUtils.hasText(packageName)) {
                    log.info(String.format("Running tests in package %s", packageName));
                }

                if (getConfiguration().getTestJar() != null) {
                    classesToRun.addAll(new JarFileTestScanner(getConfiguration().getTestJar(), getConfiguration().getIncludes()).findTestsInPackage(packageName));
                } else {
                    classesToRun.addAll(new ClassPathTestScanner(Test.class, getConfiguration().getIncludes()).findTestsInPackage(packageName));
                }
            }

            log.info(String.format("Found %s test classes to execute", classesToRun.size()));
            run(classesToRun);
        }
    }

    /**
     * Run given set of test classes with JUnit4.
     * @param classesToRun
     */
    private void run(List<TestClass> classesToRun) {
        JUnitCore junit = new JUnitCore();

        for (RunListener listener : listeners) {
            junit.addListener(listener);
        }

        junit.run(classesToRun
                .stream()
                .peek(testClass -> log.info(String.format("Running test %s", Optional.ofNullable(testClass.getMethod()).map(method -> testClass.getName() + "#" + method).orElse(testClass.getName()))))
                .map(testClass -> {
                    try {
                        Class<?> clazz;
                        if (getConfiguration().getTestJar() != null) {
                            clazz = Class.forName(testClass.getName(), false, new URLClassLoader(new URL[]{ getConfiguration().getTestJar().toURI().toURL() }, getClass().getClassLoader()));
                        } else {
                            clazz = Class.forName(testClass.getName());
                        }
                        log.debug("Found test candidate: " + testClass.getName());
                        return clazz;
                    } catch (ClassNotFoundException | MalformedURLException e) {
                        log.warn("Unable to read test class: " + testClass.getName());
                        return Void.class;
                    }
                })
                .filter(clazz -> !clazz.equals(Void.class))
                .toArray(Class[]::new));
    }

    /**
     * Adds run listener in fluent API.
     * @param listener
     */
    public JUnit4TestEngine addRunListener(RunListener listener) {
        this.listeners.add(listener);
        return this;
    }
}
