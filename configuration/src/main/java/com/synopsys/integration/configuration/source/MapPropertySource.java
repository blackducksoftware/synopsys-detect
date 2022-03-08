package com.synopsys.integration.configuration.source;

import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.configuration.util.KeyUtils;

public class MapPropertySource implements PropertySource {
    private final String givenName;
    private final Map<String, String> normalizedPropertyMap;

    public MapPropertySource(String givenName, Map<String, String> underlyingMap) {
        this.givenName = givenName;
        this.normalizedPropertyMap = Bds.of(underlyingMap)
            .toMap(entry -> KeyUtils.normalizeKey(entry.getKey()), Map.Entry::getValue);
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
