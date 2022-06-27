package com.synopsys.integration.detect.workflow.diagnostic;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.help.DetectArgumentState;

public class DiagnosticDecision {
    private final boolean isDiagnostic;

    public static DiagnosticDecision decide(DetectArgumentState detectArgumentState, PropertyConfiguration propertyConfiguration) {
        boolean isDiagnostic = detectArgumentState.isDiagnostic() || propertyConfiguration.getValueOrDefault(DetectProperties.DETECT_DIAGNOSTIC);
        return new DiagnosticDecision(isDiagnostic);
    }

    private DiagnosticDecision(boolean isDiagnostic) {
        this.isDiagnostic = isDiagnostic;
    }

    public boolean shouldCreateDiagnosticSystem() {
        return isDiagnostic;
    }
}
