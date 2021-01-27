package com.synopsys.integration.configuration.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.configuration.property.Property;

public class PropertyMasker {
    // TODO - do we need this?
    public Map<Property, String> maskRawValues(Set<Property> knownProperties, Map<String, String> rawKeyValues, Predicate<String> shouldMask) {
        Map<Property, String> masked = new HashMap<>();
        for (Map.Entry<String, String> rawKeyValue : rawKeyValues.entrySet()) {
            Optional<Property> correspondingProperty = findCorrespondingProperty(knownProperties, rawKeyValue.getKey());
            String maskedValue = maskValue(rawKeyValue.getKey(), rawKeyValue.getValue(), shouldMask);
            if (correspondingProperty.isPresent()) {
                masked.put(correspondingProperty.get(), maskedValue);
            }
        }
        return masked;
    }

    public Map<String, String> maskRawValues(Map<String, String> rawKeyValues, Predicate<String> shouldMask) {
        Map<String, String> masked = new HashMap<>();
        for (Map.Entry<String, String> rawKeyValue : rawKeyValues.entrySet()) {
            masked.put(rawKeyValue.getKey(), maskValue(rawKeyValue.getKey(), rawKeyValue.getValue(), shouldMask));
        }
        return masked;
    }

    private Optional<Property> findCorrespondingProperty(Set<Property> knownProperties, String rawKey) {
        Property correspondingProperty = null;
        for (Property property : knownProperties) {
            if (property.getKey().equals(rawKey)) {
                correspondingProperty = property;
            }
        }
        return Optional.ofNullable(correspondingProperty);
    }

    public String maskValue(String rawKey, String rawValue, Predicate<String> shouldMask) {
        String maskedValue = rawValue;
        if (shouldMask.test(rawKey)) {
            maskedValue = StringUtils.repeat('*', maskedValue.length());
        }
        return maskedValue;
    }
}
