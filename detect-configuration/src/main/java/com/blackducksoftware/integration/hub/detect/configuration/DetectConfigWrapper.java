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

    protected void setDetectProperty(DetectProperty detectProperty, String stringValue) {
        updatePropertyMap(propertyMap, detectProperty, stringValue);
    }

    private void updatePropertyMap(Map<DetectProperty, Object> propertyMap, DetectProperty detectProperty, String stringValue) {
        Object value;
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
