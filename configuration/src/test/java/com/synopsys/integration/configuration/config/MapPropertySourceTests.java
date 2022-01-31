package com.synopsys.integration.configuration.config;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.configuration.source.PropertySource;

class MapPropertySourceTests {
    @Test
    public void testNormalizesKeys() {
        PropertySource source = new MapPropertySource("test", Collections.singletonMap("CAPITAL_UNDERSCORE", "value"));
        Set<String> keys = source.getKeys();
        Assertions.assertEquals(Collections.singleton("capital.underscore"), keys);
    }

    @Test
    public void returnsKey() {
        PropertySource source = new MapPropertySource("test", Collections.singletonMap("property.key", "value"));
        Assertions.assertEquals("value", source.getValue("property.key"));
        Assertions.assertEquals("test", source.getOrigin("property.key"));
        Assertions.assertEquals("test", source.getName());
    }
}