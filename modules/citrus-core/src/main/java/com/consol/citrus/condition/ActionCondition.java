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

package com.consol.citrus.condition;

import com.consol.citrus.TestAction;
import com.consol.citrus.context.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @author Christoph Deppisch
 * @since 2.7.6
 */
public class ActionCondition extends AbstractCondition {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(ActionCondition.class);

    /** Action to execute */
    private TestAction action;

    /** Optional exception caught during action */
    private Exception caughtException;

    /**
     * Default constructor.
     */
    public ActionCondition() {
        super();
    }

    /**
     * Default constructor using test action to execute.
     * @param action
     */
    public ActionCondition(TestAction action) {
        this.action = action;
    }

    @Override
    public boolean isSatisfied(TestContext context) {
        if (action == null) {
            return false;
        }

        try {
            action.execute(context);
        } catch (Exception e) {
            this.caughtException = e;
            log.warn(String.format("Nested action did not perform as expected - %s", Optional.ofNullable(e.getMessage())
                                                                                            .map(msg -> e.getClass().getName() + ": " + msg)
                                                                                            .orElse(e.getClass().getName())));
            return false;
        }

        return true;
    }

    @Override
    public String getSuccessMessage(TestContext context) {
        return String.format("Test action condition success - action '%s' did perform as expected", getActionName());
    }

    @Override
    public String getErrorMessage(TestContext context) {
        if (caughtException != null) {
            return String.format("Failed to check test action condition - action '%s' did not perform as expected: %s", getActionName(), Optional.ofNullable(caughtException.getMessage())
                    .map(msg -> caughtException.getClass().getName() + ": " + msg)
                    .orElse(caughtException.getClass().getName()));
        } else {
            return String.format("Failed to check test action condition - action '%s' did not perform as expected", getActionName());
        }
    }

    private String getActionName() {
        return Optional.ofNullable(action).map(TestAction::getName).orElse("unknown");
    }

    /**
     * Gets the action.
     *
     * @return
     */
    public TestAction getAction() {
        return action;
    }

    /**
     * Sets the action.
     *
     * @param action
     */
    public void setAction(TestAction action) {
        this.action = action;
    }

    /**
     * Gets the caughtException.
     *
     * @return
     */
    public Exception getCaughtException() {
        return caughtException;
    }

    /**
     * Sets the caughtException.
     *
     * @param caughtException
     */
    public void setCaughtException(Exception caughtException) {
        this.caughtException = caughtException;
    }
}
