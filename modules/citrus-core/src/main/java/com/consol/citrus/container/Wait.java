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

package com.consol.citrus.container;

import com.consol.citrus.TestAction;
import com.consol.citrus.condition.Condition;
import com.consol.citrus.condition.ActionCondition;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * Pause the test execution until the condition is met or the wait time has been exceeded.
 *
 * @author Martin Maher
 * @since 2.4
 */
public class Wait extends AbstractActionContainer {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(Wait.class);

    /** Nested test action */
    private TestAction action;

    /** Condition to be met */
    private Condition condition;

    /** The total time to wait in seconds, for the condition to be met before failing */
    private String seconds;

    /** The total time to wait in milliseconds, for the condition to be met before failing */
    private String milliseconds = "5000";

    /** The time interval in milliseconds between each test of the condition */
    private String interval = "1000";

    /**
     * Default constructor.
     */
    public Wait() {
        setName("wait");
    }

    @Override
    public void doExecute(final TestContext context) {
        Boolean conditionSatisfied = null;
        long timeLeft = getWaitTimeMs(context);
        long intervalMs = getIntervalMs(context);

        if (intervalMs > timeLeft) {
            intervalMs = timeLeft;
        }

        if (condition == null) {
            condition = new ActionCondition(Optional.ofNullable(action).orElseThrow(() -> new CitrusRuntimeException("Invalid wait condition -  null")));
        }

        Callable<Boolean> callable = () -> condition.isSatisfied(context);

        while (timeLeft > 0) {
            timeLeft -= intervalMs;

            if (log.isDebugEnabled()) {
                log.debug(String.format("Waiting for condition %s", condition.getName()));
            }

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Boolean> future = executor.submit(callable);
            long checkStartTime = System.currentTimeMillis();
            try {
                conditionSatisfied = future.get(intervalMs, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | TimeoutException | ExecutionException e) {
                log.warn(String.format("Condition check interrupted with '%s'", e.getClass().getSimpleName()));
            }
            executor.shutdown();

            if (Boolean.TRUE.equals(conditionSatisfied)) {
                log.info(condition.getSuccessMessage(context));
                return;
            }

            long sleepTime = intervalMs - (System.currentTimeMillis() - checkStartTime);
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    log.warn("Interrupted during wait!", e);
                }
            }
        }

        throw new CitrusRuntimeException(condition.getErrorMessage(context));
    }

    /**
     * Gets total wait time in milliseconds. Either uses second time value or default milliseconds.
     * @param context
     * @return
     */
    private long getWaitTimeMs(TestContext context) {
        if (StringUtils.hasText(seconds)) {
            return Long.valueOf(context.replaceDynamicContentInString(seconds)) * 1000;
        } else {
            return Long.valueOf(context.replaceDynamicContentInString(milliseconds));
        }
    }

    @Override
    public Wait addTestAction(TestAction action) {
        this.action = action;
        super.addTestAction(action);
        return this;
    }

    @Override
    public TestAction getTestAction(int index) {
        if (index == 0) {
            return action;
        } else {
            throw new IndexOutOfBoundsException("Illegal index in action list:" + index);
        }
    }

    @Override
    public Wait setActions(List<TestAction> actions) {
        if (actions.size() > 1) {
            throw new CitrusRuntimeException("Invalid number of nested test actions - only one single nested action is allowed");
        }

        action = actions.get(0);
        super.setActions(actions);
        return this;
    }

    /**
     * Gets the time interval for the condition check in milliseconds.
     * @param context
     * @return
     */
    private long getIntervalMs(TestContext context) {
        return Long.valueOf(context.replaceDynamicContentInString(interval));
    }

    public String getSeconds() {
        return seconds;
    }

    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }

    public String getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(String milliseconds) {
        this.milliseconds = milliseconds;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    /**
     * Set the nested test action.
     * @param action the action to set
     */
    public void setAction(TestAction action) {
        addTestAction(action);
    }

    /**
     * Gets the action.
     * @return the action
     */
    public TestAction getAction() {
        return action;
    }

}
