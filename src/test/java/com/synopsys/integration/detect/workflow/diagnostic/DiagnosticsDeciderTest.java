package com.synopsys.integration.detect.workflow.diagnostic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.help.DetectArgumentState;

class DiagnosticsDeciderTest {
    @Test
    void commandLineDecision() {
        DetectArgumentState detectArgumentState = createDetectArgumentState(true, false);
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(Mockito.any())).thenReturn(false);

        DiagnosticsDecider diagnosticsDecider = new DiagnosticsDecider(detectArgumentState, propertyConfiguration);

        Assertions.assertTrue(diagnosticsDecider.isConfiguredForDiagnostic());
        Assertions.assertTrue(diagnosticsDecider.isDiagnostic());
        Assertions.assertFalse(diagnosticsDecider.isDiagnosticExtended());
    }

    @Test
    void commandLineDecisionExtended() {
        DetectArgumentState detectArgumentState = createDetectArgumentState(false, true);
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(Mockito.any())).thenReturn(false);

        DiagnosticsDecider diagnosticsDecider = new DiagnosticsDecider(detectArgumentState, propertyConfiguration);

        Assertions.assertTrue(diagnosticsDecider.isConfiguredForDiagnostic());
        Assertions.assertFalse(diagnosticsDecider.isDiagnostic());
        Assertions.assertTrue(diagnosticsDecider.isDiagnosticExtended());
    }

    @Test
    void propertyDecision() {
        DetectArgumentState detectArgumentState = createDetectArgumentState(false, false);
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(DetectProperties.Companion.getDETECT_DIAGNOSTIC())).thenReturn(true);
        Mockito.when(propertyConfiguration.getValueOrDefault(DetectProperties.Companion.getDETECT_DIAGNOSTIC_EXTENDED())).thenReturn(false);

        DiagnosticsDecider diagnosticsDecider = new DiagnosticsDecider(detectArgumentState, propertyConfiguration);

        Assertions.assertTrue(diagnosticsDecider.isConfiguredForDiagnostic());
        Assertions.assertTrue(diagnosticsDecider.isDiagnostic());
        Assertions.assertFalse(diagnosticsDecider.isDiagnosticExtended());
    }

    @Test
    void propertyDecisionExtended() {
        DetectArgumentState detectArgumentState = createDetectArgumentState(false, false);
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(DetectProperties.Companion.getDETECT_DIAGNOSTIC())).thenReturn(false);
        Mockito.when(propertyConfiguration.getValueOrDefault(DetectProperties.Companion.getDETECT_DIAGNOSTIC_EXTENDED())).thenReturn(true);

        DiagnosticsDecider diagnosticsDecider = new DiagnosticsDecider(detectArgumentState, propertyConfiguration);

        Assertions.assertTrue(diagnosticsDecider.isConfiguredForDiagnostic());
        Assertions.assertFalse(diagnosticsDecider.isDiagnostic());
        Assertions.assertTrue(diagnosticsDecider.isDiagnosticExtended());
    }

    @Test
    void noDiagnostic() {
        DetectArgumentState detectArgumentState = createDetectArgumentState(false, false);
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(Mockito.any())).thenReturn(false);

        DiagnosticsDecider diagnosticsDecider = new DiagnosticsDecider(detectArgumentState, propertyConfiguration);

        Assertions.assertFalse(diagnosticsDecider.isConfiguredForDiagnostic());
        Assertions.assertFalse(diagnosticsDecider.isDiagnostic());
        Assertions.assertFalse(diagnosticsDecider.isDiagnosticExtended());
    }

    private DetectArgumentState createDetectArgumentState(boolean isDiagnostic, boolean isDiagnosticExtended) {
        return new DetectArgumentState(false, false, false, false, false, null, isDiagnostic, isDiagnosticExtended, false);
    }
}