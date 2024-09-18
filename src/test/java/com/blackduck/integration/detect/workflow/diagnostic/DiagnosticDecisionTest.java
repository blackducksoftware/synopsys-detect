package com.blackduck.integration.detect.workflow.diagnostic;

import java.util.Collections;
import java.util.HashMap;

import com.blackduck.integration.detect.configuration.DetectProperties;
import com.blackduck.integration.detect.configuration.help.DetectArgumentState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.blackduck.integration.configuration.config.PropertyConfiguration;
import com.blackduck.integration.configuration.source.MapPropertySource;

class DiagnosticDecisionTest {
    private static final String TEST_PROPERTY_SOURCE_NAME = "TEST_PROPERTIES";

    @Test
    void commandLineDecision() {
        PropertyConfiguration propertyConfiguration = createPropertyConfiguration(false);
        DetectArgumentState detectArgumentState = createDetectArgumentState(true);

        DiagnosticDecision diagnosticDecision = DiagnosticDecision.decide(detectArgumentState, propertyConfiguration);

        Assertions.assertTrue(diagnosticDecision.shouldCreateDiagnosticSystem());
    }

    @Test
    void propertyDecision() {
        PropertyConfiguration propertyConfiguration = createPropertyConfiguration(true);
        DetectArgumentState detectArgumentState = createDetectArgumentState(false);

        DiagnosticDecision diagnosticDecision = DiagnosticDecision.decide(detectArgumentState, propertyConfiguration);

        Assertions.assertTrue(diagnosticDecision.shouldCreateDiagnosticSystem());
    }

    @Test
    void noDiagnostic() {
        PropertyConfiguration propertyConfiguration = createPropertyConfiguration(false);
        DetectArgumentState detectArgumentState = createDetectArgumentState(false);

        DiagnosticDecision diagnosticDecision = DiagnosticDecision.decide(detectArgumentState, propertyConfiguration);

        Assertions.assertFalse(diagnosticDecision.shouldCreateDiagnosticSystem());
    }

    private PropertyConfiguration createPropertyConfiguration(boolean isDiagnostic) {
        HashMap<String, String> propertySourceMap = new HashMap<>();
        propertySourceMap.put(DetectProperties.DETECT_DIAGNOSTIC.getKey(), String.valueOf(isDiagnostic));
        MapPropertySource mapPropertySource = new MapPropertySource(TEST_PROPERTY_SOURCE_NAME, propertySourceMap);

        return new PropertyConfiguration(Collections.singletonList(mapPropertySource), Collections.emptySortedMap());
    }

    private DetectArgumentState createDetectArgumentState(boolean isDiagnostic) {
        return new DetectArgumentState(false, false, false, false, false, false, null, isDiagnostic, false);
    }

}