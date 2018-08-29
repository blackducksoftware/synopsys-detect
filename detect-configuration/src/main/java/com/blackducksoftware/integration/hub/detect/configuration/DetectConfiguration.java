/**
 * detect-configuration
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
package com.blackducksoftware.integration.hub.detect.configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.configuration.HubServerConfigBuilder;

public class DetectConfiguration {
    private final Logger logger = LoggerFactory.getLogger(DetectConfiguration.class);
    private final DetectPropertySource detectPropertySource;
    private final DetectPropertyMap detectPropertyMap;

    public DetectConfiguration(final DetectPropertySource detectPropertySource, final DetectPropertyMap detectPropertyMap) {
        this.detectPropertySource = detectPropertySource;
        this.detectPropertyMap = detectPropertyMap;
    }

    private final Set<DetectProperty> actuallySetValues = new HashSet<>();

    public boolean getShouldIgnoreSetValue(final DetectProperty property) {
        return !actuallySetValues.contains(property);
    }

    // TODO: Remove override code in version 6.
    public void init() {
        Arrays.stream(DetectProperty.values()).forEach(currentProperty -> {
            if (!detectPropertyMap.containsDetectProperty(currentProperty)) {
                final DetectProperty override = fromDeprecatedToOverride(currentProperty);
                if (override != null) {
                    final boolean currentExists = detectPropertySource.containsDetectProperty(currentProperty.getPropertyName());
                    final boolean overrideExists = detectPropertySource.containsDetectProperty(override.getPropertyName());
                    DetectProperty chosenProperty = override;
                    if (currentExists && !overrideExists) {
                        chosenProperty = currentProperty;
                    }
                    final String value = detectPropertySource.getDetectProperty(chosenProperty.getPropertyName(), chosenProperty.getDefaultValue());
                    detectPropertyMap.setDetectProperty(currentProperty, value);
                    detectPropertyMap.setDetectProperty(override, value);
                } else {
                    detectPropertyMap.setDetectProperty(currentProperty, detectPropertySource.getDetectProperty(currentProperty.getPropertyName(), currentProperty.getDefaultValue()));
                }
            }
        });
    }

    // TODO: Remove in version 6.
    private DetectProperty fromDeprecatedToOverride(final DetectProperty detectProperty) {
        if (DetectPropertyDeprecations.PROPERTY_OVERRIDES.containsKey(detectProperty)) {
            return DetectPropertyDeprecations.PROPERTY_OVERRIDES.get(detectProperty);
        } else {
            return null;
        }
    }

    // TODO: Remove in version 6.
    private DetectProperty fromOverrideToDeprecated(final DetectProperty detectProperty) {
        final Optional<DetectProperty> found = DetectPropertyDeprecations.PROPERTY_OVERRIDES.entrySet().stream()
                .filter(it -> it.getValue().equals(detectProperty))
                .map(it -> it.getKey())
                .findFirst();

        return found.orElse(null);
    }

    public Set<String> getBlackduckPropertyKeys() {
        final Set<String> providedKeys = detectPropertySource.getBlackduckPropertyKeys();
        final Set<String> allKeys = new HashSet<>(providedKeys);
        Arrays.stream(DetectProperty.values()).forEach(currentProperty -> {
            final String propertyName = currentProperty.getPropertyName();
            if (propertyName.startsWith(HubServerConfigBuilder.HUB_SERVER_CONFIG_ENVIRONMENT_VARIABLE_PREFIX) || propertyName.startsWith(HubServerConfigBuilder.HUB_SERVER_CONFIG_PROPERTY_KEY_PREFIX)) {
                allKeys.add(propertyName);
            } else if (propertyName.startsWith(DetectPropertySource.BLACKDUCK_PROPERTY_PREFIX) || propertyName.startsWith(DetectPropertySource.BLACKDUCK_ENVIRONMENT_PREFIX)) {
                allKeys.add(propertyName);
            }
        });
        return allKeys;
    }

    public Map<String, String> getPhoneHomeProperties() {
        return getKeys(detectPropertySource.getPhoneHomePropertyKeys());
    }

    public Map<String, String> getBlackduckProperties() {
        return getKeys(getBlackduckPropertyKeys());
    }

    public Map<String, String> getDockerProperties() {
        return getKeysWithoutPrefix(detectPropertySource.getDockerPropertyKeys(), DetectPropertySource.DOCKER_PROPERTY_PREFIX);
    }

    public Map<String, String> getDockerEnvironmentProperties() {
        return getKeysWithoutPrefix(detectPropertySource.getDockerEnvironmentKeys(), DetectPropertySource.DOCKER_ENVIRONMENT_PREFIX);
    }

    // Redirect to the underlying map
    public boolean getBooleanProperty(final DetectProperty detectProperty) {
        return detectPropertyMap.getBooleanProperty(detectProperty);
    }

    public Long getLongProperty(final DetectProperty detectProperty) {
        return detectPropertyMap.getLongProperty(detectProperty);
    }

    public Integer getIntegerProperty(final DetectProperty detectProperty) {
        return detectPropertyMap.getIntegerProperty(detectProperty);
    }

    public String[] getStringArrayProperty(final DetectProperty detectProperty) {
        return detectPropertyMap.getStringArrayProperty(detectProperty);
    }

    public String getProperty(final DetectProperty detectProperty) {
        return detectPropertyMap.getProperty(detectProperty);
    }

    public String getPropertyValueAsString(final DetectProperty detectProperty) {
        return detectPropertyMap.getPropertyValueAsString(detectProperty);
    }

    /**
     * DetectOptionManager and ConfigurationManager
     */
    public void setDetectProperty(final DetectProperty detectProperty, final String stringValue) {
        // TODO: Remove overrides in a future version of detect.
        final DetectProperty override = fromDeprecatedToOverride(detectProperty);
        final DetectProperty deprecated = fromOverrideToDeprecated(detectProperty);

        detectPropertyMap.setDetectProperty(detectProperty, stringValue);
        actuallySetValues.add(detectProperty);
        if (override != null) {
            detectPropertyMap.setDetectProperty(override, stringValue);
        } else if (deprecated != null) {
            detectPropertyMap.setDetectProperty(deprecated, stringValue);
        }
    }

    public Map<DetectProperty, Object> getCurrentProperties() {
        return new HashMap<>(detectPropertyMap.getUnderlyingPropertyMap());// return an immutable copy
    }

    private Map<String, String> getKeys(final Set<String> keys) {
        return getKeysWithoutPrefix(keys, "");
    }

    private Map<String, String> getKeysWithoutPrefix(final Set<String> keys, final String prefix) {
        final Map<String, String> properties = new HashMap<>();
        for (final String detectKey : keys) {
            final Optional<DetectProperty> propertyValue = getPropertyFromString(detectKey);
            String value = null;
            if (propertyValue.isPresent()) {
                value = getPropertyValueAsString(propertyValue.get());
            }
            if (StringUtils.isBlank(value)) {
                value = detectPropertySource.getProperty(detectKey);
            }
            if (StringUtils.isNotBlank(value)) {
                final String dockerKey = getKeyWithoutPrefix(detectKey, prefix);
                properties.put(dockerKey, value);
            }
        }
        return properties;
    }

    private Optional<DetectProperty> getPropertyFromString(final String detectKey) {
        try {
            final String casedKey = detectKey.toUpperCase().replaceAll("\\.", "_");
            return Optional.of(DetectProperty.valueOf(casedKey));
        } catch (final Exception e) {
            logger.trace("Could not convert key to detect key: " + detectKey);
            return Optional.empty();
        }
    }

    private String getKeyWithoutPrefix(final String key, final String prefix) {
        return key.substring(prefix.length());
    }

}
