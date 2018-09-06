package com.blackducksoftware.integration.hub.detect.configuration;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class DetectPropertyConverter {

    public String convertFromValue(final DetectPropertyType type, final Object objectValue) {
        String displayValue = "";
        if (DetectPropertyType.STRING == type) {
            displayValue = (String) objectValue;
        } else if (DetectPropertyType.STRING_ARRAY == type) {
            displayValue = StringUtils.join((String[]) objectValue, ",");
        } else if (null != objectValue) {
            displayValue = objectValue.toString();
        }
        return displayValue;
    }

    public Object convertToValue(final DetectPropertyType type, final String stringValue) {
        final Object value;
        if (DetectPropertyType.BOOLEAN == type) {
            value = convertToBoolean(stringValue);
        } else if (DetectPropertyType.LONG == type) {
            value = convertToLong(stringValue);
        } else if (DetectPropertyType.INTEGER == type) {
            value = convertToInt(stringValue);
        } else if (DetectPropertyType.STRING_ARRAY == type) {
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
