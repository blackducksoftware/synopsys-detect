package com.synopsys.integration.detect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.config.MapPropertySource;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.config.PropertySource;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.type.OperatingSystemType;
import com.synopsys.integration.detect.util.TildeInPathResolver;

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

        HashMap<String, String> values = new HashMap<>();
        values.put(givenKeyFull, givenValue);
        List<PropertySource> propertySources = new ArrayList<>();
        propertySources.add(new MapPropertySource("test", values));
        PropertyConfiguration propertyConfiguration = new PropertyConfiguration(propertySources);

        Map<String, String> phoneHomePropertiesMap = propertyConfiguration.getRaw(DetectProperties.Companion.getPHONEHOME_PASSTHROUGH());
        Assertions.assertEquals(givenValue, phoneHomePropertiesMap.get(givenKeyPhoneHomePart));
    }

    @Test
    public void testDeprecated() {
        HashMap<String, String> values = new HashMap<>();
        values.put(DetectProperties.Companion.getDETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS().getKey(), "2");
        List<PropertySource> propertySources = new ArrayList<>();
        propertySources.add(new MapPropertySource("test", values));
        PropertyConfiguration propertyConfiguration = new PropertyConfiguration(propertySources);
        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(propertyConfiguration, new TildeInPathResolver("home", OperatingSystemType.WINDOWS, false));
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = detectConfigurationFactory.createBlackDuckSignatureScannerOptions();
        Assertions.assertEquals(2, (int) blackDuckSignatureScannerOptions.getParallelProcessors());
    }
}
