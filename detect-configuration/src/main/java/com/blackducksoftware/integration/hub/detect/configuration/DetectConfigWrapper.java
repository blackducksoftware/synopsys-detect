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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.core.env.ConfigurableEnvironment;

public class DetectConfigWrapper {

    private final ConfigurableEnvironment configurableEnvironment;

    private final Map<DetectProperty, Object> propertyMap = new HashMap<>();

    public DetectConfigWrapper(final ConfigurableEnvironment configurableEnvironment) {
        this.configurableEnvironment = configurableEnvironment;
    }

    public void init() {
        Arrays.stream(DetectProperty.values()).forEach(detectProperty -> {
            final String stringValue = configurableEnvironment.getProperty(detectProperty.getPropertyName(), detectProperty.getDefaultValue());
            updatePropertyMap(propertyMap, detectProperty, stringValue);
        });
    }

    public boolean getBooleanProperty(final DetectProperty detectProperty) {
        return (boolean) propertyMap.get(detectProperty);
    }

    public long getLongProperty(final DetectProperty detectProperty) {
        return (long) propertyMap.get(detectProperty);
    }

    public int getIntegerProperty(final DetectProperty detectProperty) {
        return (int) propertyMap.get(detectProperty);
    }

    public String[] getStringArrayProperty(final DetectProperty detectProperty) {
        return (String[]) propertyMap.get(detectProperty);
    }

    public String getProperty(final DetectProperty detectProperty) {
        return (String) propertyMap.get(detectProperty);
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
            value = stringValue;
        }
        propertyMap.put(detectProperty, value);
    }

    private String[] convertStringArray(final String string) {
        if (null == string) {
            return null;
        } else {
            return string.split(",");
        }
    }

    private int convertInt(final String integerString) {
        return NumberUtils.toInt(integerString);
    }

    private long convertLong(final String longString) {
        if (null == longString) {
            return 0L;
        }
        try {
            return Long.valueOf(longString);
        } catch (final NumberFormatException e) {
            return 0L;
        }
    }

    private boolean convertBoolean(final String booleanString) {
        return BooleanUtils.toBoolean(booleanString);
    }
}
