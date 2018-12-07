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

package com.consol.citrus.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.testng.AbstractTestNGUnitTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.*;

/**
 * @author Christoph Deppisch
 * @since 2.7
 */
public class AbstractAsyncTestActionTest extends AbstractTestNGUnitTest {

    @Test
    public void testOnSuccess() throws Exception {
        CompletableFuture<Boolean> result = new CompletableFuture<>();

        new AbstractAsyncTestAction() {
            @Override
            public void doExecuteAsync(TestContext context) {
                log.info("Success!");
            }

            @Override
            public void onSuccess(TestContext context) {
                result.complete(true);
            }

            @Override
            public void onError(TestContext context, Throwable error) {
                result.completeExceptionally(error);
            }
        }.execute(context);

        Assert.assertTrue(result.get(1000, TimeUnit.MILLISECONDS));
    }

    @Test(expectedExceptions = ExecutionException.class, expectedExceptionsMessageRegExp = "com.consol.citrus.exceptions.CitrusRuntimeException: Failed!")
    public void testOnError() throws Exception {
        CompletableFuture<Boolean> result = new CompletableFuture<>();

        new AbstractAsyncTestAction() {
            @Override
            public void doExecuteAsync(TestContext context) {
                throw new CitrusRuntimeException("Failed!");
            }

            @Override
            public void onError(TestContext context, Throwable error) {
                result.completeExceptionally(error);
            }

            @Override
            public void onSuccess(TestContext context) {
                result.complete(false);
            }
        }.execute(context);

        Assert.assertTrue(result.get(1000, TimeUnit.MILLISECONDS));
    }

}