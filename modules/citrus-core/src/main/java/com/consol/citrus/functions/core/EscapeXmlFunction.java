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

package com.consol.citrus.functions.core;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.InvalidFunctionUsageException;
import com.consol.citrus.functions.Function;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Escapes XML fragment with escaped characters for '<', '>'.
 * 
 * @author Christoph Deppisch
 */
public class EscapeXmlFunction implements Function {

    /**
      * {@inheritDoc}
      */
    public String execute(List<String> parameterList, TestContext context) {
        if (CollectionUtils.isEmpty(parameterList) || parameterList.size() != 1) {
            throw new InvalidFunctionUsageException("Invalid function parameter usage! Expected single parameter but found: " + parameterList.size());
        }
        
        return StringEscapeUtils.escapeXml(parameterList.get(0));
    }

}
