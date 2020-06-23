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
        DiagnosticsDecision diagnosticsDecision = diagnosticsDecider.decide();

        Assertions.assertTrue(diagnosticsDecision.isConfiguredForDiagnostic);
        Assertions.assertTrue(diagnosticsDecision.isDiagnostic);
        Assertions.assertFalse(diagnosticsDecision.isDiagnosticExtended);
    }

    @Test
    void commandLineDecisionExtended() {
        DetectArgumentState detectArgumentState = createDetectArgumentState(false, true);
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(Mockito.any())).thenReturn(false);

        DiagnosticsDecider diagnosticsDecider = new DiagnosticsDecider(detectArgumentState, propertyConfiguration);
        DiagnosticsDecision diagnosticsDecision = diagnosticsDecider.decide();

        Assertions.assertTrue(diagnosticsDecision.isConfiguredForDiagnostic);
        Assertions.assertFalse(diagnosticsDecision.isDiagnostic);
        Assertions.assertTrue(diagnosticsDecision.isDiagnosticExtended);
    }

    @Test
    void propertyDecision() {
        DetectArgumentState detectArgumentState = createDetectArgumentState(false, false);
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(DetectProperties.Companion.getDETECT_DIAGNOSTIC())).thenReturn(true);
        Mockito.when(propertyConfiguration.getValueOrDefault(DetectProperties.Companion.getDETECT_DIAGNOSTIC_EXTENDED())).thenReturn(false);

        DiagnosticsDecider diagnosticsDecider = new DiagnosticsDecider(detectArgumentState, propertyConfiguration);
        DiagnosticsDecision diagnosticsDecision = diagnosticsDecider.decide();

        Assertions.assertTrue(diagnosticsDecision.isConfiguredForDiagnostic);
        Assertions.assertTrue(diagnosticsDecision.isDiagnostic);
        Assertions.assertFalse(diagnosticsDecision.isDiagnosticExtended);
    }

    @Test
    void propertyDecisionExtended() {
        DetectArgumentState detectArgumentState = createDetectArgumentState(false, false);
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(DetectProperties.Companion.getDETECT_DIAGNOSTIC())).thenReturn(false);
        Mockito.when(propertyConfiguration.getValueOrDefault(DetectProperties.Companion.getDETECT_DIAGNOSTIC_EXTENDED())).thenReturn(true);

        DiagnosticsDecider diagnosticsDecider = new DiagnosticsDecider(detectArgumentState, propertyConfiguration);
        DiagnosticsDecision diagnosticsDecision = diagnosticsDecider.decide();

        Assertions.assertTrue(diagnosticsDecision.isConfiguredForDiagnostic);
        Assertions.assertFalse(diagnosticsDecision.isDiagnostic);
        Assertions.assertTrue(diagnosticsDecision.isDiagnosticExtended);
    }

    @Test
    void noDiagnostic() {
        DetectArgumentState detectArgumentState = createDetectArgumentState(false, false);
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(Mockito.any())).thenReturn(false);

        DiagnosticsDecider diagnosticsDecider = new DiagnosticsDecider(detectArgumentState, propertyConfiguration);
        DiagnosticsDecision diagnosticsDecision = diagnosticsDecider.decide();

        Assertions.assertFalse(diagnosticsDecision.isConfiguredForDiagnostic);
        Assertions.assertFalse(diagnosticsDecision.isDiagnostic);
        Assertions.assertFalse(diagnosticsDecision.isDiagnosticExtended);
    }

    private DetectArgumentState createDetectArgumentState(boolean isDiagnostic, boolean isDiagnosticExtended) {
        return new DetectArgumentState(false, false, false, false, false, null, isDiagnostic, isDiagnosticExtended, false);
    }
}