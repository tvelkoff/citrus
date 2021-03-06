/*
 * Copyright 2006-2015 the original author or authors.
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

package com.consol.citrus.javadsl.runner;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
@Test
public class ParallelTestRunnerIT extends TestNGCitrusTestRunner {
    
    @CitrusTest
    public void parallelContainer() {
        parallel().actions(
            sleep(150),
            sequential().actions(
                    sleep(100),
                    echo("1")
            ),
            echo("2"),
            echo("3"),
            iterate().condition("i lt= 5").index("i")
                .actions(
                    echo("10")
                )
        );
        
        assertException().when(
            parallel().actions(
                sleep(150),
                sequential().actions(
                        sleep(100),
                        fail("This went wrong too"),
                        echo("1")
                ),
                echo("2"),
                fail("This went wrong too"),
                echo("3"),
                iterate().condition("i lt= 5").index("i")
                    .actions(
                        echo("10")
                    )
            )
        );
    }
}