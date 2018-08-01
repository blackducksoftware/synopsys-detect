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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.log.Slf4jIntLogger;
import com.blackducksoftware.integration.rest.connection.UnauthenticatedRestConnection;
import com.blackducksoftware.integration.rest.connection.UnauthenticatedRestConnectionBuilder;
import com.blackducksoftware.integration.rest.proxy.ProxyInfo;
import com.blackducksoftware.integration.rest.proxy.ProxyInfoBuilder;

public class DetectConfiguration {
    private final Logger logger = LoggerFactory.getLogger(DetectConfiguration.class);
    private final Map<DetectProperty, Object> propertyMap = new HashMap<>();
    private final DetectPropertySource detectPropertySource;

    public DetectConfiguration(final DetectPropertySource detectPropertySource) {
        this.detectPropertySource = detectPropertySource;
    }

    public void init() {
        Arrays.stream(DetectProperty.values()).forEach(detectProperty -> {
            updatePropertyMap(propertyMap, detectProperty, detectPropertySource.getDetectProperty(detectProperty.getPropertyName(), detectProperty.getDefaultValue()));
        });
    }

    public ProxyInfo getHubProxyInfo() throws DetectUserFriendlyException {
        final ProxyInfoBuilder proxyInfoBuilder = new ProxyInfoBuilder();
        proxyInfoBuilder.setHost(getProperty(DetectProperty.BLACKDUCK_HUB_PROXY_HOST));
        proxyInfoBuilder.setPort(getProperty(DetectProperty.BLACKDUCK_HUB_PROXY_PORT));
        proxyInfoBuilder.setUsername(getProperty(DetectProperty.BLACKDUCK_HUB_PROXY_USERNAME));
        proxyInfoBuilder.setPassword(getProperty(DetectProperty.BLACKDUCK_HUB_PROXY_PASSWORD));
        proxyInfoBuilder.setIgnoredProxyHosts(getProperty(DetectProperty.BLACKDUCK_HUB_PROXY_IGNORED_HOSTS));
        proxyInfoBuilder.setNtlmDomain(getProperty(DetectProperty.BLACKDUCK_HUB_PROXY_NTLM_DOMAIN));
        proxyInfoBuilder.setNtlmWorkstation(getProperty(DetectProperty.BLACKDUCK_HUB_PROXY_NTLM_WORKSTATION));
        ProxyInfo proxyInfo = ProxyInfo.NO_PROXY_INFO;
        try {
            proxyInfo = proxyInfoBuilder.build();
        } catch (final IllegalStateException e) {
            throw new DetectUserFriendlyException(String.format("Your proxy configuration is not valid: %s", e.getMessage()), e, ExitCodeType.FAILURE_PROXY_CONNECTIVITY);
        }
        return proxyInfo;
    }

    public UnauthenticatedRestConnection createUnauthenticatedRestConnection(final String url) throws DetectUserFriendlyException {
        final UnauthenticatedRestConnectionBuilder restConnectionBuilder = new UnauthenticatedRestConnectionBuilder();
        restConnectionBuilder.setBaseUrl(url);
        restConnectionBuilder.setTimeout(getIntegerProperty(DetectProperty.BLACKDUCK_HUB_TIMEOUT));
        restConnectionBuilder.applyProxyInfo(getHubProxyInfo());
        restConnectionBuilder.setLogger(new Slf4jIntLogger(logger));
        restConnectionBuilder.setAlwaysTrustServerCertificate(getBooleanProperty(DetectProperty.BLACKDUCK_HUB_TRUST_CERT));

        return restConnectionBuilder.build();
    }

    public Map<String, String> getPhoneHomeProperties() {
        return getKeys(detectPropertySource.getPhoneHomePropertyKeys());
    }

    public Map<String, String> getBlackduckProperties() {
        return getKeys(detectPropertySource.getBlackduckPropertyKeys());
    }

    public Map<String, String> getDockerProperties() {
        return getKeysWithoutPrefix(detectPropertySource.getDockerPropertyKeys(), DetectPropertySource.DOCKER_PROPERTY_PREFIX);
    }

    public Map<String, String> getDockerEnvironmentProperties() {
        return getKeysWithoutPrefix(detectPropertySource.getDockerEnvironmentKeys(), DetectPropertySource.DOCKER_ENVIRONMENT_PREFIX);
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
            return Optional.of(DetectProperty.valueOf(detectKey));
        } catch (final Exception e) {
            return Optional.empty();
        }
    }

    private String getKeyWithoutPrefix(final String key, final String prefix) {
        return key.substring(prefix.length());
    }

    public boolean getBooleanProperty(final DetectProperty detectProperty) {
        final Object value = propertyMap.get(detectProperty);
        if (null == value) {
            return false;
        }
        return (boolean) value;
    }

    public Long getLongProperty(final DetectProperty detectProperty) {
        final Object value = propertyMap.get(detectProperty);
        if (null == value) {
            return null;
        }
        return (long) value;
    }

    public Integer getIntegerProperty(final DetectProperty detectProperty) {
        final Object value = propertyMap.get(detectProperty);
        if (null == value) {
            return null;
        }
        return (int) value;
    }

    public String[] getStringArrayProperty(final DetectProperty detectProperty) {
        return (String[]) propertyMap.get(detectProperty);
    }

    public String getProperty(final DetectProperty detectProperty) {
        return (String) propertyMap.get(detectProperty);
    }

    public String getPropertyValueAsString(final DetectProperty detectProperty) {
        final Object objectValue = propertyMap.get(detectProperty);
        String displayValue = "";
        if (DetectPropertyType.STRING == detectProperty.getPropertyType()) {
            displayValue = (String) objectValue;
        } else if (DetectPropertyType.STRING_ARRAY == detectProperty.getPropertyType()) {
            displayValue = StringUtils.join((String[]) objectValue, ",");
        } else if (null != objectValue) {
            displayValue = objectValue.toString();
        }
        return displayValue;
    }

    /**
     * DetectOptionManager, ConfigurationManager, and TildeInPathResolver should be the only classes using this method
     */
    public void setDetectProperty(final DetectProperty detectProperty, final String stringValue) {
        updatePropertyMap(propertyMap, detectProperty, stringValue);
    }

    /**
     * DetectOptionManager, ConfigurationManager, and TildeInPathResolver should be the only classes using this method
     */
    public Map<DetectProperty, Object> getPropertyMap() {
        return propertyMap;
    }

    public Map<String, String> getPropertyKeyMap() {
        final Map<String, String> keyMap = new HashMap<>();
        for (final Entry<DetectProperty, Object> entry : propertyMap.entrySet()) {
            if (entry.getKey().getPropertyType() == DetectPropertyType.STRING) {
                keyMap.put(entry.getKey().getPropertyName(), (String) entry.getValue());
            }
        }
        return keyMap;
    }

    private void updatePropertyMap(final Map<DetectProperty, Object> propertyMap, final DetectProperty detectProperty, final String stringValue) {
        final Object value;
        if (DetectPropertyType.BOOLEAN == detectProperty.getPropertyType()) {
            value = convertBoolean(stringValue);
        } else if (DetectPropertyType.LONG == detectProperty.getPropertyType()) {
            value = convertLong(stringValue);
        } else if (DetectPropertyType.INTEGER == detectProperty.getPropertyType()) {
            value = convertInt(stringValue);
        } else if (DetectPropertyType.STRING_ARRAY == detectProperty.getPropertyType()) {
            value = convertStringArray(stringValue);
        } else {
            if (null == stringValue) {
                value = "";
            } else {
                value = stringValue;
            }
        }
        propertyMap.put(detectProperty, value);
    }

    private String[] convertStringArray(final String string) {
        if (null == string) {
            return new String[0];
        } else {
            return string.split(",");
        }
    }

    private Integer convertInt(final String integerString) {
        if (null == integerString) {
            return null;
        }
        return NumberUtils.toInt(integerString);
    }

    private Long convertLong(final String longString) {
        if (null == longString) {
            return null;
        }
        try {
            return Long.valueOf(longString);
        } catch (final NumberFormatException e) {
            return 0L;
        }
    }

    private Boolean convertBoolean(final String booleanString) {
        if (null == booleanString) {
            return null;
        }
        return BooleanUtils.toBoolean(booleanString);
    }
}
