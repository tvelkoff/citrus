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

package com.consol.citrus.arquillian.lifecycle;

import com.consol.citrus.Citrus;
import com.consol.citrus.arquillian.CitrusExtensionConstants;
import com.consol.citrus.arquillian.configuration.CitrusConfiguration;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.AfterSuite;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Observes Arquillian before and after suite events in order to execute corresponding lifecycle phases in Citrus.
 *
 * @author Christoph Deppisch
 * @since 2.2
 */
public class CitrusLifecycleHandler {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(CitrusLifecycleHandler.class);

    @Inject
    private Instance<CitrusConfiguration> configurationInstance;

    @Inject
    private Instance<Citrus> citrusInstance;

    public void beforeSuite(@Observes(precedence = CitrusExtensionConstants.LIFECYCLE_PRECEDENCE) BeforeSuite event) {
        log.debug("Starting Citrus before suite lifecycle");
        citrusInstance.get().beforeSuite(configurationInstance.get().getSuiteName());
    }

    public void afterSuite(@Observes AfterSuite event) {
        log.debug("Starting Citrus after suite lifecycle");
        citrusInstance.get().afterSuite(configurationInstance.get().getSuiteName());

        log.debug("Closing Citrus Spring application context");
        ((ConfigurableApplicationContext)citrusInstance.get().getApplicationContext()).close();
    }
}
