package com.blackduck.integration.configuration.source;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.blackduck.integration.configuration.util.KeyUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.blackduck.integration.common.util.Bds;

public class JavaPropertiesPropertySource implements PropertySource {
    private final String givenName;
    private final Map<String, String> normalizedPropertyMap;

    public JavaPropertiesPropertySource(String givenName, Properties properties) {
        this.givenName = givenName;
        this.normalizedPropertyMap = Bds.of(properties.stringPropertyNames())
            .toMap(KeyUtils::normalizeKey, properties::getProperty);
    }

    @Override
    @NotNull
    public Boolean hasKey(String key) {
        return normalizedPropertyMap.containsKey(key);
    }

    @Override
    @NotNull
    public Set<String> getKeys() {
        return normalizedPropertyMap.keySet();
    }

    @Override
    @Nullable
    public String getValue(String key) {
        return normalizedPropertyMap.getOrDefault(key, null);
    }

    @Override
    @NotNull
    public String getOrigin(String key) {
        return givenName;
    }

    @Override
    @NotNull
    public String getName() {
        return givenName;
    }
}
