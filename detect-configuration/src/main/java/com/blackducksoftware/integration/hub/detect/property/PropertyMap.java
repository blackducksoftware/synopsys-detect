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
package com.blackducksoftware.integration.hub.detect.property;

import java.util.HashMap;
import java.util.Map;

/**
 * DetectConfiguration should be the only class that uses this.
 */

public class PropertyMap {
    private final Map<DetectProperty, Object> propertyMap = new HashMap<>();
    private final DetectPropertyConverter detectPropertyConverter = new DetectPropertyConverter();

    public boolean containsDetectProperty(final DetectProperty detectProperty) {
        return propertyMap.containsKey(detectProperty);
    }

    public String getPropertyValueAsString(final DetectProperty detectProperty) {
        final Object objectValue = propertyMap.get(detectProperty);
        return detectPropertyConverter.convertFromValue(detectProperty.getPropertyType(), objectValue);
    }

    public void setDetectProperty(final DetectProperty detectProperty, final String stringValue) {
        final Object value = detectPropertyConverter.convertToValue(detectProperty.getPropertyType(), stringValue);
        propertyMap.put(detectProperty, value);
    }

    public Map<DetectProperty, Object> getUnderlyingPropertyMap() {
        return propertyMap;
    }

    // Typed Getters

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
}
