/**
 * detect-configuration
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Instead of Source of Truth, this is the initial configuration.
public class DetectConfiguration {
    private final Logger logger = LoggerFactory.getLogger(DetectConfiguration.class);
    private final DetectPropertySource detectPropertySource;
    private final DetectPropertyMap detectPropertyMap;

    public DetectConfiguration(final DetectPropertySource detectPropertySource, final DetectPropertyMap detectPropertyMap) {
        this.detectPropertySource = detectPropertySource;
        this.detectPropertyMap = detectPropertyMap;
        init();
    }

    private final Set<DetectProperty> actuallySetValues = new HashSet<>();

    public boolean wasPropertyActuallySet(final DetectProperty property) {
        return actuallySetValues.contains(property);
    }

    // TODO: Remove override code in version 6.
    private void init() {
        Arrays.stream(DetectProperty.values()).forEach(currentProperty -> {
            final DetectProperty override = fromDeprecatedToOverride(currentProperty);
            final DetectProperty deprecated = fromOverrideToDeprecated(currentProperty);
            if (override == null && deprecated == null) {
                handleStandardProperty(currentProperty);
            } else if (override != null) {
                handleDeprecatedProperty(currentProperty, override);// a deprecated property has an override
            } else if (deprecated != null) {
                handleOverrideProperty(currentProperty, deprecated);// an override property has a deprecated property
            }
        });
        //TODO: Find a better way to do this - or - hopefully remove this if the scan cli gets better.
        final String bdScanPaths = detectPropertySource.getProperty("BD_HUB_SCAN_PATH");
        if (StringUtils.isNotBlank(bdScanPaths)) {
            logger.warn("The environment variable BD_HUB_SCAN_PATH was set but you should use --" + DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS.getPropertyKey() + " instead.");
            final List<String> values = new ArrayList<>();
            values.addAll(Arrays.asList(getStringArrayProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS, PropertyAuthority.None)));
            values.addAll(Arrays.asList(bdScanPaths.split(",")));
            setDetectProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS, values.stream().collect(Collectors.joining(",")));
        }
    }

    private void handleStandardProperty(final DetectProperty currentProperty) {
        final String value = detectPropertySource.getDetectProperty(currentProperty);
        detectPropertyMap.setDetectProperty(currentProperty, value);
        if (detectPropertySource.containsDetectProperty(currentProperty)) {
            actuallySetValues.add(currentProperty);
        }
    }

    private void handleDeprecatedProperty(final DetectProperty currentProperty, final DetectProperty overrideProperty) {
        final boolean currentExists = detectPropertySource.containsDetectProperty(currentProperty);
        if (currentExists) {
            final boolean overrideExists = detectPropertySource.containsDetectProperty(overrideProperty);
            if (!overrideExists) {
                // current exists but override does not, so we choose to use the current and set the override. Otherwise, ignore this property, it should be set by the override property.
                setChosenProperty(currentProperty, overrideProperty, true);
            }
        }
    }

    private void handleOverrideProperty(final DetectProperty currentProperty, final DetectProperty deprecatedProperty) {
        final boolean currentExists = detectPropertySource.containsDetectProperty(currentProperty);
        final boolean deprecatedExists = detectPropertySource.containsDetectProperty(deprecatedProperty);
        if (currentExists) {
            setChosenProperty(currentProperty, deprecatedProperty, true);
            if (deprecatedExists) { // even though it wasn't chosen, if deprecated was set we want to mark it as set.
                actuallySetValues.add(deprecatedProperty);
            }
        } else if (!currentExists && !deprecatedExists) {
            setChosenProperty(currentProperty, deprecatedProperty, false);
        }
    }

    private void setChosenProperty(final DetectProperty chosenProperty, final DetectProperty unchosenProperty, final boolean actuallySetChosen) {
        final String value = detectPropertySource.getDetectProperty(chosenProperty);
        detectPropertyMap.setDetectProperty(chosenProperty, value);
        detectPropertyMap.setDetectProperty(unchosenProperty, value);
        if (actuallySetChosen) {
            actuallySetValues.add(chosenProperty);
        }
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

    public Map<String, String> getPhoneHomeProperties() {
        return getKeys(detectPropertySource.getPhoneHomePropertyKeys());
    }

    public Map<String, String> getProperties(Set<String> keys) {
        return getKeys(keys);
    }

    public Map<String, String> getDockerProperties() {
        return getKeysWithoutPrefix(detectPropertySource.getDockerPropertyKeys(), DetectPropertySource.DOCKER_PROPERTY_PREFIX);
    }

    public Map<String, String> getDockerEnvironmentProperties() {
        return getKeysWithoutPrefix(detectPropertySource.getDockerEnvironmentKeys(), DetectPropertySource.DOCKER_ENVIRONMENT_PREFIX);
    }

    private boolean isLocked = false;

    public void lock() {
        isLocked = true;
        logger.info("Configuration has finished.");
    }

    private void authorize(final DetectProperty property, final PropertyAuthority authority) {
        if (!isLocked)
            return;
        if (property.getPropertyAuthority() != authority) {
            throw new RuntimeException("Authority " + authority.toString() + " may not access " + property.getPropertyKey() + " whose authority is " + property.getPropertyAuthority());
        }
    }

    // Redirect to the underlying map
    public boolean getBooleanProperty(final DetectProperty detectProperty, final PropertyAuthority authority) {
        authorize(detectProperty, authority);
        return detectPropertyMap.getBooleanProperty(detectProperty);
    }

    public Long getLongProperty(final DetectProperty detectProperty, final PropertyAuthority authority) {
        authorize(detectProperty, authority);
        return detectPropertyMap.getLongProperty(detectProperty);
    }

    public Integer getIntegerProperty(final DetectProperty detectProperty, final PropertyAuthority authority) {
        authorize(detectProperty, authority);
        return detectPropertyMap.getIntegerProperty(detectProperty);
    }

    public String[] getStringArrayProperty(final DetectProperty detectProperty, final PropertyAuthority authority) {
        authorize(detectProperty, authority);
        return detectPropertyMap.getStringArrayProperty(detectProperty);
    }

    public Optional<File> getFileProperty(final DetectProperty detectProperty, final PropertyAuthority authority) {
        final String value = getPropertyValueAsString(detectProperty, authority);
        File file = null;
        if (StringUtils.isNotBlank(value)) {
            file = new File(value);
        }
        return Optional.ofNullable(file);
    }

    public String getProperty(final DetectProperty detectProperty, final PropertyAuthority authority) {
        authorize(detectProperty, authority);
        return detectPropertyMap.getProperty(detectProperty);
    }

    /**
     * Same as <code>getProperty()</code>, but returns an optional after performing a <code>StringUtils.isBlank()</code> check
     */
    public Optional<String> getOptionalProperty(final DetectProperty detectProperty, final PropertyAuthority authority) {
        authorize(detectProperty, authority);
        final String property = getProperty(detectProperty, authority);
        Optional<String> optionalProperty = Optional.empty();
        if (StringUtils.isNotBlank(property)) {
            optionalProperty = Optional.of(property);
        }

        return optionalProperty;
    }

    public String getPropertyValueAsString(final DetectProperty detectProperty, final PropertyAuthority authority) {
        authorize(detectProperty, authority);
        return detectPropertyMap.getPropertyValueAsString(detectProperty);
    }

    public void setDetectProperty(final DetectProperty detectProperty, final String stringValue) {
        if (isLocked) {
            throw new RuntimeException("Detect configuration has been locked. You may not change properties.");
        }

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

    //Does not require authorization because you are bananas
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
                value = getPropertyValueAsString(propertyValue.get(), PropertyAuthority.None);
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
