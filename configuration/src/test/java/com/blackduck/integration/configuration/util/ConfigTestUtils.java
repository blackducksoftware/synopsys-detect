package com.blackduck.integration.configuration.util;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySortedMap;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.blackduck.integration.common.util.Bds;
import com.blackduck.integration.configuration.config.PropertyConfiguration;
import com.blackduck.integration.configuration.source.MapPropertySource;
import com.blackduck.integration.configuration.source.PropertySource;

public class ConfigTestUtils {
    @NotNull
    public static PropertyConfiguration emptyConfig() {
        return new PropertyConfiguration(emptyList(), emptySortedMap());
    }

    @SafeVarargs
    @NotNull
    public static PropertyConfiguration configOf(@NotNull Pair<String, String>... properties) {
        return configOf(propertySourceOf("map", properties));
    }

    @SafeVarargs
    @NotNull
    public static PropertySource propertySourceOf(@NotNull String name, @NotNull Map.Entry<String, String>... properties) {
        return new MapPropertySource(name, Bds.mapOfEntries(properties));
    }

    @NotNull
    public static PropertyConfiguration configOf(@NotNull Map<String, String> properties) {
        PropertySource propertySource = new MapPropertySource("map", properties);
        return configOf(propertySource);
    }

    @NotNull
    public static PropertyConfiguration configOf(@NotNull PropertySource... propertySources) {
        return new PropertyConfiguration(Arrays.asList(propertySources), emptySortedMap());
    }
}