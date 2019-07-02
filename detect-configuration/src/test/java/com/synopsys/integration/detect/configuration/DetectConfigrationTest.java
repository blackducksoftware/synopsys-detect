package com.synopsys.integration.detect.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class DetectConfigrationTest {

    @Test
    public void testPhoneHomePassthroughProperties() {
        final DetectPropertySource detectPropertySource = Mockito.mock(DetectPropertySource.class);
        final DetectPropertyMap detectPropertyMap = Mockito.mock(DetectPropertyMap.class);

        final String givenKeyPhoneHomePart = "x.y.z";
        final String givenKeyFull = "detect.phone.home.passthrough." + givenKeyPhoneHomePart;
        final String givenValue = "testValue";

        final Set<String> phoneHomePropertyKeys = new HashSet<>();
        phoneHomePropertyKeys.add(givenKeyFull);
        Mockito.when(detectPropertySource.getPhoneHomePropertyKeys()).thenReturn(phoneHomePropertyKeys);
        Mockito.when(detectPropertySource.getProperty(givenKeyFull)).thenReturn(givenValue);
        final DetectConfiguration config = new DetectConfiguration(detectPropertySource, detectPropertyMap);

        final Map<String, String> phoneHomePropertiesMap = config.getPhoneHomeProperties();

        assertEquals(givenValue, phoneHomePropertiesMap.get(givenKeyPhoneHomePart));
    }
}
