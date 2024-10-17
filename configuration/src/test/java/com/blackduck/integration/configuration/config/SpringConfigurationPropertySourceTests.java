package com.blackduck.integration.configuration.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import com.blackduck.integration.configuration.property.base.NullableAlikeProperty;
import com.blackduck.integration.configuration.property.types.string.NullableStringProperty;
import com.blackduck.integration.configuration.source.PropertySource;
import com.blackduck.integration.configuration.source.SpringConfigurationPropertySource;

class SpringConfigurationPropertySourceTests {
    @Test
    public void verifySpringReturnsValue() throws InvalidPropertyException {
        MockEnvironment m = new MockEnvironment();
        m.setProperty("example.key", "value");

        List<PropertySource> sources = new ArrayList<>(SpringConfigurationPropertySource.fromConfigurableEnvironment(m, true));
        PropertyConfiguration config = new PropertyConfiguration(sources, Collections.emptySortedMap());

        NullableAlikeProperty<String> property = new NullableStringProperty("example.key");
        Assertions.assertEquals(Optional.of("value"), config.getValue(property));
        Assertions.assertEquals(Optional.of("mockProperties"), config.getPropertySource(property));
    }
}