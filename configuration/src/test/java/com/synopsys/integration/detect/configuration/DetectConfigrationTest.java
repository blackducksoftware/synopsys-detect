package com.synopsys.integration.detect.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

//import com.synopsys.integration.detect.DetectInfo;
//import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
//import com.synopsys.integration.detect.help.DetectOption;
//import com.synopsys.integration.detect.help.DetectOptionManager;
//import com.synopsys.integration.detect.type.OperatingSystemType;

public class DetectConfigrationTest {

    @Test
    public void testPhoneHomePassthroughProperties() {
//        final DetectPropertySource detectPropertySource = Mockito.mock(DetectPropertySource.class);
//        final DetectPropertyMap detectPropertyMap = Mockito.mock(DetectPropertyMap.class);
//
//        final String givenKeyPhoneHomePart = "x.y.z";
//        final String givenKeyFull = "detect.phone.home.passthrough." + givenKeyPhoneHomePart;
//        final String givenValue = "testValue";
//
//        final Set<String> phoneHomePropertyKeys = new HashSet<>();
//        phoneHomePropertyKeys.add(givenKeyFull);
//        Mockito.when(detectPropertySource.getPhoneHomePropertyKeys()).thenReturn(phoneHomePropertyKeys);
//        Mockito.when(detectPropertySource.getProperty(givenKeyFull)).thenReturn(givenValue);
//
//        //TODO - fix
//        final DetectConfig config = new DetectConfig(new ArrayList<>());
//
//        final Map<String, String> phoneHomePropertiesMap = config.getPhoneHomeProperties();
//
//        assertEquals(givenValue, phoneHomePropertiesMap.get(givenKeyPhoneHomePart));
    }

    @Test
    public void testDeprecated() {
//        //TODO: Proper option tests. This is a work-in-progress sample test for checking properties final values. Feel free to delete if causes you issues.
//        final Map<String, String> properties = new HashMap<>();
//        properties.put(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS.getPropertyKey(), "2");
//
//        final FakePropertySource propertySource = new FakePropertySource(properties);
//        final DetectPropertySource detectPropertySource = new DetectPropertySource(propertySource);
//        final DetectPropertyMap detectPropertyMap = new DetectPropertyMap();
//
//        //TODO - fix
//        final DetectConfig config = new DetectConfig(new ArrayList<>());
//
//        final DetectInfo info = new DetectInfo("6.0.0", 6, OperatingSystemType.WINDOWS);
//
//        //TODO - fix
//        final DetectOptionManager detectOptionManager = new DetectOptionManager();
//        //        final TildeInPathResolver tildeInPathResolver = new TildeInPathResolver(DetectConfigurationManager.USER_HOME, info.getCurrentOs());
//        //        final DetectConfigurationManager detectConfigurationManager = new DetectConfigurationManager(tildeInPathResolver, config);
//        //        detectConfigurationManager.process(detectOptionManager.getDetectOptions());
//        //        detectOptionManager.postConfigurationProcessedInit();
//
//        final List<DetectOption> options = detectOptionManager.getDetectOptions();
//        options.forEach(option -> {
//            if (option.getDetectProperty() == DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS) {
//                Assertions.assertEquals("2", option.getFinalValue());
//                Assertions.assertSame(DetectOption.FinalValueType.SUPPLIED, option.getFinalValueType());
//            }
//        });

    }
}
