package com.synopsys.integration.configuration.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

public class PropertyMasker {
    public Map<String, String> maskRawValues(Map<String, String> rawKeyValues, Predicate<String> shouldMask) {
        Map<String, String> masked = new HashMap<>();
        for (Map.Entry<String, String> rawKeyValue : rawKeyValues.entrySet()) {
            masked.put(rawKeyValue.getKey(), maskValue(rawKeyValue.getKey(), rawKeyValue.getValue(), shouldMask));
        }
        return masked;
    }

    public String maskValue(String rawKey, String rawValue, Predicate<String> shouldMask) {
        String maskedValue = rawValue;
        if (shouldMask.test(rawKey)) {
            maskedValue = StringUtils.repeat('*', maskedValue.length());
        }
        return maskedValue;
    }
}
