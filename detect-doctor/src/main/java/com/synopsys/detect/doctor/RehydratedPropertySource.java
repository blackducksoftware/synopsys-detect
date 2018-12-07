package com.synopsys.detect.doctor;

import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.hub.detect.property.PropertySource;

public class RehydratedPropertySource implements PropertySource {

    private final Map<String, String> values;

    public RehydratedPropertySource(Map<String, String> values) {
        this.values = values;
    }

    @Override
    public boolean containsProperty(final String key) {
        return values.containsKey(key);
    }

    @Override
    public String getProperty(final String key, final String defaultValue) {
        if (values.containsKey(key)) {
            return values.get(key);
        } else {
            return defaultValue;
        }
    }

    @Override
    public String getProperty(final String key) {
        return values.get(key);
    }

    @Override
    public Set<String> getPropertyKeys() {
        return values.keySet();
    }
}
