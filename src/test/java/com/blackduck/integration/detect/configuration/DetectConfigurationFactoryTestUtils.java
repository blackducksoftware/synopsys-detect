package com.blackduck.integration.detect.configuration;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.blackduck.integration.common.util.Bds;
import com.blackduck.integration.configuration.config.PropertyConfiguration;
import com.blackduck.integration.configuration.property.Property;
import com.blackduck.integration.configuration.property.types.path.SimplePathResolver;
import com.blackduck.integration.configuration.source.MapPropertySource;
import com.blackduck.integration.configuration.source.PropertySource;

public class DetectConfigurationFactoryTestUtils {
    @SafeVarargs
    public static DetectConfigurationFactory spyFactoryOf(Pair<Property, String>... properties) {
        return Mockito.spy(factoryOf(properties));
    }

    @SafeVarargs
    public static DetectConfigurationFactory factoryOf(Pair<Property, String>... properties) {
        Map<String, String> propertyMap = Bds.of(properties).toMap(pair -> pair.getLeft().getKey(), Pair::getRight);
        PropertySource inMemoryPropertySource = new MapPropertySource("test", propertyMap);
        PropertyConfiguration propertyConfiguration = new PropertyConfiguration(Collections.singletonList(inMemoryPropertySource), Collections.emptySortedMap());
        DetectPropertyConfiguration detectPropertyConfiguration = new DetectPropertyConfiguration(propertyConfiguration, new SimplePathResolver());
        return new DetectConfigurationFactory(detectPropertyConfiguration, new Gson());
    }
    public static DetectConfigurationFactory scanSettingsFactoryOf(Map<String, String> propertyMap, Pair<Property, String>... scanSettingsProperties) {
        Map<String, String> scanSettingsPropertyMap = Bds.of(scanSettingsProperties).toMap(pair -> pair.getLeft().getKey(), Pair::getRight);
        SortedMap<String, String> scanSettingsMap = new TreeMap<>(scanSettingsPropertyMap);
        PropertySource inMemoryPropertySource = new MapPropertySource("test", propertyMap);
        PropertyConfiguration propertyConfiguration = new PropertyConfiguration(Collections.singletonList(inMemoryPropertySource), scanSettingsMap);
        DetectPropertyConfiguration detectPropertyConfiguration = new DetectPropertyConfiguration(propertyConfiguration, new SimplePathResolver());
        return new DetectConfigurationFactory(detectPropertyConfiguration, new Gson());
    }
}