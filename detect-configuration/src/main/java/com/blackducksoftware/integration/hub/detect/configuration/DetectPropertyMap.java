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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.blackducksoftware.integration.hub.detect.property.PropertyType;

/**
 * DetectConfiguration should be the only class that uses this.
 */

public class DetectPropertyMap {
    private final Map<DetectProperty, Object> propertyMap = new HashMap<>();

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
        if (PropertyType.STRING == detectProperty.getPropertyType()) {
            displayValue = (String) objectValue;
        } else if (PropertyType.STRING_ARRAY == detectProperty.getPropertyType()) {
            displayValue = StringUtils.join((String[]) objectValue, ",");
        } else if (null != objectValue) {
            displayValue = objectValue.toString();
        }
        return displayValue;
    }

    public void setDetectProperty(final DetectProperty detectProperty, final String stringValue) {
        updatePropertyMap(propertyMap, detectProperty, stringValue);
    }

    public boolean containsDetectProperty(final DetectProperty detectProperty) {
        return propertyMap.containsKey(detectProperty);
    }

    public Map<DetectProperty, Object> getUnderlyingPropertyMap() {
        return propertyMap;
    }

    private void updatePropertyMap(final Map<DetectProperty, Object> propertyMap, final DetectProperty detectProperty, final String stringValue) {
        final Object value;
        if (PropertyType.BOOLEAN == detectProperty.getPropertyType()) {
            value = convertBoolean(stringValue);
        } else if (PropertyType.LONG == detectProperty.getPropertyType()) {
            value = convertLong(stringValue);
        } else if (PropertyType.INTEGER == detectProperty.getPropertyType()) {
            value = convertInt(stringValue);
        } else if (PropertyType.STRING_ARRAY == detectProperty.getPropertyType()) {
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
        if (booleanString.equals("")) { //Support defaulting to true (--key is equivalent to --key=true)
            return true;
        }
        return BooleanUtils.toBoolean(booleanString);
    }
}
