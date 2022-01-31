package com.synopsys.integration.detect.configuration;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.types.path.SimplePathResolver;
import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.configuration.source.PropertySource;

class DetectConfigurationFactoryTestUtils {
    @SafeVarargs
    public static DetectConfigurationFactory spyFactoryOf(Pair<Property, String>... properties) {
        return Mockito.spy(factoryOf(properties));
    }

    @SafeVarargs
    public static DetectConfigurationFactory factoryOf(Pair<Property, String>... properties) {
        Map<String, String> propertyMap = Bds.of(properties).toMap(pair -> pair.getLeft().getKey(), Pair::getRight);
        PropertySource inMemoryPropertySource = new MapPropertySource("test", propertyMap);
        PropertyConfiguration propertyConfiguration = new PropertyConfiguration(Collections.singletonList(inMemoryPropertySource));
        DetectPropertyConfiguration detectPropertyConfiguration = new DetectPropertyConfiguration(propertyConfiguration, new SimplePathResolver());
        return new DetectConfigurationFactory(detectPropertyConfiguration, new Gson());
    }
}