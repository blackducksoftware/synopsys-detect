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
package com.synopsys.integration.detect.property;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class PropertyConverter {

    public String convertFromValue(final PropertyType type, final Object objectValue) {
        String displayValue = "";
        if (PropertyType.STRING == type) {
            displayValue = (String) objectValue;
        } else if (PropertyType.STRING_ARRAY == type) {
            displayValue = StringUtils.join((String[]) objectValue, ",");
        } else if (null != objectValue) {
            displayValue = objectValue.toString();
        }
        return displayValue;
    }

    public Object convertToValue(final PropertyType type, final String stringValue) {
        final Object value;
        if (PropertyType.BOOLEAN == type) {
            value = convertToBoolean(stringValue);
        } else if (PropertyType.LONG == type) {
            value = convertToLong(stringValue);
        } else if (PropertyType.INTEGER == type) {
            value = convertToInt(stringValue);
        } else if (PropertyType.STRING_ARRAY == type) {
            value = convertToStringArray(stringValue);
        } else {
            if (null == stringValue) {
                value = "";
            } else {
                value = stringValue;
            }
        }
        return value;
    }

    private String[] convertToStringArray(final String string) {
        if (null == string) {
            return new String[0];
        } else {
            return string.split(",");
        }
    }

    private Integer convertToInt(final String integerString) {
        if (null == integerString) {
            return null;
        }
        return NumberUtils.toInt(integerString);
    }

    private Long convertToLong(final String longString) {
        if (null == longString) {
            return null;
        }
        try {
            return Long.valueOf(longString);
        } catch (final NumberFormatException e) {
            return 0L;
        }
    }

    private Boolean convertToBoolean(final String booleanString) {
        if (null == booleanString) {
            return null;
        }
        return BooleanUtils.toBoolean(booleanString);
    }

}
