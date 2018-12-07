/*
 * Copyright 2006-2014 the original author or authors.
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

package com.consol.citrus.config.xml;

import com.consol.citrus.channel.ChannelEndpoint;
import com.consol.citrus.channel.ChannelEndpointConfiguration;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.endpoint.EndpointConfiguration;

/**
 * Channel endpoint parser.
 *
 * @author Christoph Deppisch
 * @since 1.4
 */
public class ChannelEndpointParser extends AbstractChannelEndpointParser {

    @Override
    protected Class<? extends Endpoint> getEndpointClass() {
        return ChannelEndpoint.class;
    }

    @Override
    protected Class<? extends EndpointConfiguration> getEndpointConfigurationClass() {
        return ChannelEndpointConfiguration.class;
    }
}
