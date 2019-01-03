/**
 * detect-configuration
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.configuration;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.property.PropertySource;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder;

public class DetectPropertySource {
    public static final String PHONE_HOME_PROPERTY_PREFIX = "detect.phone.home.passthrough.";
    public static final String DOCKER_PROPERTY_PREFIX = "detect.docker.passthrough.";
    public static final String DOCKER_ENVIRONMENT_PREFIX = "DETECT_DOCKER_PASSTHROUGH_";
    public static final String BLACKDUCK_PROPERTY_PREFIX = "blackduck."; // TODO: Remove these in major version 6 and when hub common supports them.
    public static final String BLACKDUCK_ENVIRONMENT_PREFIX = "BLACKDUCK_"; // TODO: Remove these in major version 6 and when hub common supports them.

    private final Set<String> blackduckPropertyKeys = new HashSet<>();
    private final Set<String> dockerPropertyKeys = new HashSet<>();
    private final Set<String> dockerEnvironmentKeys = new HashSet<>();
    private final Set<String> phoneHomePropertyKeys = new HashSet<>();

    private PropertySource propertySource;

    public DetectPropertySource(PropertySource propertySource) {
        this.propertySource = propertySource;

        // TODO: Remove redirection from "blackduck.hub." to "blackduck." in version 6.
        for (final String propertyName : propertySource.getPropertyKeys()) {
            if (StringUtils.isNotBlank(propertyName)) {
                if (propertyName.startsWith(DOCKER_PROPERTY_PREFIX)) {
                    dockerPropertyKeys.add(propertyName);
                } else if (propertyName.startsWith(DOCKER_ENVIRONMENT_PREFIX)) {
                    dockerEnvironmentKeys.add(propertyName);
                } else if (propertyName.startsWith(PHONE_HOME_PROPERTY_PREFIX)) {
                    phoneHomePropertyKeys.add(propertyName);
                } else if (propertyName.startsWith(BlackDuckServerConfigBuilder.BLACKDUCK_SERVER_CONFIG_ENVIRONMENT_VARIABLE_PREFIX) || propertyName.startsWith(BlackDuckServerConfigBuilder.BLACKDUCK_SERVER_CONFIG_PROPERTY_KEY_PREFIX)) {
                    blackduckPropertyKeys.add(propertyName);
                } else if (propertyName.startsWith(BLACKDUCK_PROPERTY_PREFIX) || propertyName.startsWith(BLACKDUCK_ENVIRONMENT_PREFIX)) {
                    blackduckPropertyKeys.add(propertyName);
                }
            }
        }
    }

    public boolean containsDetectProperty(final DetectProperty property) {
        return propertySource.containsProperty(property.getPropertyName());
    }

    public String getDetectProperty(final DetectProperty property) {
        return propertySource.getProperty(property.getPropertyName(), property.getDefaultValue());
    }

    public String getProperty(String property) {
        return propertySource.getProperty(property);
    }

    public Set<String> getBlackduckPropertyKeys() {
        return blackduckPropertyKeys;
    }

    public Set<String> getDockerPropertyKeys() {
        return dockerPropertyKeys;
    }

    public Set<String> getDockerEnvironmentKeys() {
        return dockerEnvironmentKeys;
    }

    public Set<String> getPhoneHomePropertyKeys() {
        return phoneHomePropertyKeys;
    }
}
