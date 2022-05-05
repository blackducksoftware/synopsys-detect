package com.synopsys.integration.detect.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.types.path.SimplePathResolver;
import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.configuration.source.PropertySource;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;

public class DetectConfigurationTest {
    @Test
    public void testPhoneHomePassthroughProperties() {
        final String givenKeyPhoneHomePart = "x.y.z";
        final String givenKeyFull = "detect.phone.home.passthrough." + givenKeyPhoneHomePart;
        final String givenValue = "testValue";

        HashMap<String, String> values = new HashMap<>();
        values.put(givenKeyFull, givenValue);
        List<PropertySource> propertySources = new ArrayList<>();
        propertySources.add(new MapPropertySource("test", values));
        PropertyConfiguration propertyConfiguration = new PropertyConfiguration(propertySources);

        Map<String, String> phoneHomePropertiesMap = propertyConfiguration.getRaw(DetectProperties.PHONEHOME_PASSTHROUGH);
        assertEquals(givenValue, phoneHomePropertiesMap.get(givenKeyPhoneHomePart));
    }

    @Test
    public void testGenericProperty() {
        HashMap<String, String> values = new HashMap<>();
        values.put(DetectProperties.DETECT_PROJECT_CODELOCATION_PREFIX.getKey(), "some_prefix");
        List<PropertySource> propertySources = new ArrayList<>();
        propertySources.add(new MapPropertySource("test", values));
        PropertyConfiguration propertyConfiguration = new PropertyConfiguration(propertySources);
        DetectPropertyConfiguration detectPropertyConfiguration = new DetectPropertyConfiguration(propertyConfiguration, new SimplePathResolver());
        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(detectPropertyConfiguration, new Gson());
        BdioOptions bdioOptions = detectConfigurationFactory.createBdioOptions();
        assertTrue(bdioOptions.getProjectCodeLocationPrefix().isPresent());
        assertEquals("some_prefix", bdioOptions.getProjectCodeLocationPrefix().get());
    }

}
