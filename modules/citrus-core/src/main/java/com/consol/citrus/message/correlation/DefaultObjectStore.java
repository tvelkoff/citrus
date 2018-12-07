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

package com.consol.citrus.message.correlation;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Default object store implementation works on simple in memory hash map.
 *
 * @author Christoph Deppisch
 * @since 2.1
 */
public class DefaultObjectStore<T> extends ConcurrentHashMap<String, T> implements ObjectStore<T> {

    @Override
    public void add(String correlationKey, T object) {
        super.put(correlationKey, object);
    }

    @Override
    public T remove(String correlationKey) {
        return super.remove(correlationKey);
    }
}
