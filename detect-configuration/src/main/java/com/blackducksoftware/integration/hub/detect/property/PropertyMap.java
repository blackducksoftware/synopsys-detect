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
package com.blackducksoftware.integration.hub.detect.property;

import java.util.HashMap;
import java.util.Map;

/**
 * DetectConfiguration should be the only class that uses this.
 */

public class PropertyMap<T> {
    private final Map<T, Object> propertyMap = new HashMap<>();
    private final PropertyConverter propertyKeyConverter = new PropertyConverter();

    public boolean containsProperty(final T propertyKey) {
        return propertyMap.containsKey(propertyKey);
    }

    public String getPropertyValueAsString(final T propertyKey, final PropertyType type) {
        final Object objectValue = propertyMap.get(propertyKey);
        return propertyKeyConverter.convertFromValue(type, objectValue);
    }

    public void setProperty(final T propertyKey, final PropertyType type, final String stringValue) {
        final Object value = propertyKeyConverter.convertToValue(type, stringValue);
        propertyMap.put(propertyKey, value);
    }

    public Map<T, Object> getUnderlyingPropertyMap() {
        return propertyMap;
    }

    // Typed Getters

    public boolean getBooleanProperty(final T propertyKey) {
        final Object value = propertyMap.get(propertyKey);
        if (null == value) {
            return false;
        }
        return (boolean) value;
    }

    public Long getLongProperty(final T propertyKey) {
        final Object value = propertyMap.get(propertyKey);
        if (null == value) {
            return null;
        }
        return (long) value;
    }

    public Integer getIntegerProperty(final T propertyKey) {
        final Object value = propertyMap.get(propertyKey);
        if (null == value) {
            return null;
        }
        return (int) value;
    }

    public String[] getStringArrayProperty(final T propertyKey) {
        return (String[]) propertyMap.get(propertyKey);
    }

    public String getProperty(final T propertyKey) {
        return (String) propertyMap.get(propertyKey);
    }
}
