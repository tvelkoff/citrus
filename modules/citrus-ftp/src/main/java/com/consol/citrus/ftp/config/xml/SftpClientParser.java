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

package com.consol.citrus.ftp.config.xml;

import com.consol.citrus.config.util.BeanDefinitionParserUtils;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.endpoint.EndpointConfiguration;
import com.consol.citrus.ftp.client.SftpClient;
import com.consol.citrus.ftp.client.SftpEndpointConfiguration;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * @author Christoph Deppisch
 * @since 2.7.5
 */
public class SftpClientParser extends FtpClientParser {

    @Override
    protected void parseEndpointConfiguration(BeanDefinitionBuilder endpointConfiguration, Element element, ParserContext parserContext) {
        super.parseEndpointConfiguration(endpointConfiguration, element, parserContext);

        BeanDefinitionParserUtils.setPropertyValue(endpointConfiguration, element.getAttribute("private-key-path"), "privateKeyPath");
        BeanDefinitionParserUtils.setPropertyValue(endpointConfiguration, element.getAttribute("private-key-password"), "privateKeyPassword");
        BeanDefinitionParserUtils.setPropertyValue(endpointConfiguration, element.getAttribute("strict-host-checking"), "strictHostChecking");
        BeanDefinitionParserUtils.setPropertyValue(endpointConfiguration, element.getAttribute("known-hosts-path"), "knownHosts");
    }

    @Override
    protected Class<? extends Endpoint> getEndpointClass() {
        return SftpClient.class;
    }

    @Override
    protected Class<? extends EndpointConfiguration> getEndpointConfigurationClass() {
        return SftpEndpointConfiguration.class;
    }
}
