/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.detect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

import com.blackducksoftware.integration.hub.detect.configuration.ConfigurationManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectPropertySource;
import com.blackducksoftware.integration.hub.detect.help.DetectArgumentStateParser;
import com.blackducksoftware.integration.hub.detect.help.DetectOptionManager;
import com.blackducksoftware.integration.hub.detect.help.html.HelpHtmlWriter;
import com.blackducksoftware.integration.hub.detect.help.print.DetectConfigurationPrinter;
import com.blackducksoftware.integration.hub.detect.help.print.DetectInfoPrinter;
import com.blackducksoftware.integration.hub.detect.help.print.HelpPrinter;
import com.blackducksoftware.integration.hub.detect.interactive.InteractiveManager;
import com.blackducksoftware.integration.hub.detect.interactive.mode.DefaultInteractiveMode;
import com.blackducksoftware.integration.hub.detect.property.PropertyMap;
import com.blackducksoftware.integration.hub.detect.util.TildeInPathResolver;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.synopsys.integration.util.IntegrationEscapeUtil;

import freemarker.template.Configuration;

public class DetectBootBeanConfiguration {
    //From Spring
    private final ConfigurableEnvironment configurableEnvironment;
    //Shared
    private final Gson gson;
    private final JsonParser jsonParser;
    private final Configuration configuration;

    @Autowired
    public DetectBootBeanConfiguration(final ConfigurableEnvironment configurableEnvironment, Gson gson, JsonParser jsonParser, Configuration configuration, final Gson gson1, final JsonParser jsonParser1,
        final Configuration configuration1) {
        this.configurableEnvironment = configurableEnvironment;
        this.gson = gson1;
        this.jsonParser = jsonParser1;
        this.configuration = configuration1;
    }

    @Bean
    public IntegrationEscapeUtil integrationEscapeUtil() {
        return new IntegrationEscapeUtil();
    }

    @Bean
    public DetectInfo detectInfo() {
        return new DetectInfo();
    }

    @Bean
    public HelpPrinter helpPrinter() {
        return new HelpPrinter();
    }

    @Bean
    public DetectInfoPrinter detectInfoPrinter() {
        return new DetectInfoPrinter();
    }

    @Bean
    public DetectConfigurationPrinter detectConfigurationPrinter() {
        return new DetectConfigurationPrinter();
    }

    @Bean
    public TildeInPathResolver tildeInPathResolver() {
        return new TildeInPathResolver(ConfigurationManager.USER_HOME, detectInfo().getCurrentOs());
    }

    @Bean
    public DetectConfiguration detectConfiguration() {
        return new DetectConfiguration(detectPropertySource(), propertyMap());
    }

    @Bean
    public PropertyMap propertyMap() {
        return new PropertyMap();
    }

    @Bean
    public ConfigurationManager configurationManager() {
        return new ConfigurationManager(tildeInPathResolver(), detectConfiguration());
    }

    @Bean
    public DetectOptionManager detectOptionManager() {
        return new DetectOptionManager(detectConfiguration(), detectInfo());
    }

    @Bean
    public HelpHtmlWriter helpHtmlWriter() {
        return new HelpHtmlWriter(detectOptionManager(), configuration());
    }

    @Bean
    public DetectArgumentStateParser detectArgumentStateParser() {
        return new DetectArgumentStateParser();
    }

    @Bean
    public DefaultInteractiveMode defaultInteractiveMode() {
        return new DefaultInteractiveMode(hubServiceManager(), detectOptionManager());
    }

    @Bean
    public InteractiveManager interactiveManager() {
        return new InteractiveManager(detectOptionManager(), defaultInteractiveMode());
    }

    @Bean
    public DetectPropertySource detectPropertySource() {
        return new DetectPropertySource(configurableEnvironment);
    }


}
