package com.synopsys.integration.detect.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.types.path.SimplePathResolver;
import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.configuration.source.PropertySource;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;

//import com.synopsys.integration.detect.DetectInfo;
//import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
//import com.synopsys.integration.detect.help.DetectOption;
//import com.synopsys.integration.detect.help.DetectOptionManager;
//import com.synopsys.integration.detect.type.OperatingSystemType;

public class DetectConfigrationTest {

    @Test
    public void testPhoneHomePassthroughProperties() {
        final String givenKeyPhoneHomePart = "x.y.z";
        final String givenKeyFull = "detect.phone.home.passthrough." + givenKeyPhoneHomePart;
        final String givenValue = "testValue";

        final HashMap<String, String> values = new HashMap<>();
        values.put(givenKeyFull, givenValue);
        final List<PropertySource> propertySources = new ArrayList<>();
        propertySources.add(new MapPropertySource("test", values));
        final PropertyConfiguration propertyConfiguration = new PropertyConfiguration(propertySources);

        final Map<String, String> phoneHomePropertiesMap = propertyConfiguration.getRaw(DetectProperties.Companion.getPHONEHOME_PASSTHROUGH());
        Assertions.assertEquals(givenValue, phoneHomePropertiesMap.get(givenKeyPhoneHomePart));
    }

    @Test
    public void testDeprecated() {
        final HashMap<String, String> values = new HashMap<>();
        values.put(DetectProperties.Companion.getDETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS().getKey(), "2");
        final List<PropertySource> propertySources = new ArrayList<>();
        propertySources.add(new MapPropertySource("test", values));
        final PropertyConfiguration propertyConfiguration = new PropertyConfiguration(propertySources);
        final DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(propertyConfiguration, new SimplePathResolver());
        final BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = detectConfigurationFactory.createBlackDuckSignatureScannerOptions();
        Assertions.assertEquals(2, (int) blackDuckSignatureScannerOptions.getParallelProcessors());
    }
}
