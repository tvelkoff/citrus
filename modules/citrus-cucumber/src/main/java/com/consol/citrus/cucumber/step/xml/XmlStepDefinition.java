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

package com.consol.citrus.cucumber.step.xml;

import com.consol.citrus.cucumber.container.StepTemplate;
import cucumber.api.java.ObjectFactory;
import cucumber.runtime.*;
import gherkin.pickles.PickleStep;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Special step definition that runs a XML step definition template on execution.
 * @author Christoph Deppisch
 * @since 2.6
 */
public class XmlStepDefinition implements StepDefinition {

    private final JdkPatternArgumentMatcher argumentMatcher;
    private final List<ParameterInfo> parameters;

    private final ObjectFactory objectFactory;

    private final StepTemplate stepTemplate;

    public XmlStepDefinition(StepTemplate stepTemplate, ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
        this.stepTemplate = stepTemplate;

        this.argumentMatcher = new JdkPatternArgumentMatcher(stepTemplate.getPattern());
        this.parameters = ParameterInfo.fromTypes(stepTemplate.getParameterTypes());
    }

    @Override
    public List<Argument> matchedArguments(PickleStep step) {
        return argumentMatcher.argumentsFrom(step.getText());
    }

    @Override
    public String getLocation(boolean detail) {
        return stepTemplate.getName();
    }

    @Override
    public Integer getParameterCount() {
        return parameters.size();
    }

    @Override
    public ParameterInfo getParameterType(int n, Type argumentType) throws IndexOutOfBoundsException {
        return parameters.get(n);
    }

    @Override
    public void execute(String language, Object[] args) throws Throwable {
        objectFactory.getInstance(XmlSteps.class).execute(stepTemplate, args);
    }

    @Override
    public boolean isDefinedAt(StackTraceElement stackTraceElement) {
        return stackTraceElement.getClassName().equals(XmlSteps.class.getName());
    }

    @Override
    public String getPattern() {
        return stepTemplate.getPattern().pattern();
    }

    @Override
    public boolean isScenarioScoped() {
        return false;
    }
}
