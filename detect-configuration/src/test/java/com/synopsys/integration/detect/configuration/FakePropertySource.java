package com.synopsys.integration.detect.configuration;

import java.util.Map;
import java.util.Set;

import com.synopsys.integration.detect.property.PropertySource;

public class FakePropertySource implements PropertySource {
    private final Map<String, String> properties;

    public FakePropertySource(final Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public boolean containsProperty(final String key) {
        return properties.containsKey(key);
    }

    @Override
    public String getProperty(final String key, final String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }

    @Override
    public String getProperty(final String key) {
        return properties.get(key);
    }

    @Override
    public Set<String> getPropertyKeys() {
        return properties.keySet();
    }
}
