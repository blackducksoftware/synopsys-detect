package com.synopsys.integration.configuration.config;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PropertyMaskerTest {
    @Test
    public void testMaskRawValues() {
        Map<String, String> rawValues = new HashMap<>();
        rawValues.put("blackduck.password", "password");
        rawValues.put("blackduck.username", "password");

        PropertyMasker propertyMasker = new PropertyMasker();
        Map<String, String> maskedRawValues = propertyMasker.maskRawValues(rawValues, key -> key.contains("password"));

        Assertions.assertEquals("********", maskedRawValues.get("blackduck.password"));
        Assertions.assertEquals("password", maskedRawValues.get("blackduck.username"));
    }
}
