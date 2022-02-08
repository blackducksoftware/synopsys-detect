package com.synopsys.integration.detect.workflow.diagnostic;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.help.DetectArgumentState;

public class DiagnosticDecision {
    private final boolean isExtended;
    private final boolean isDiagnostic;

    public static DiagnosticDecision decide(DetectArgumentState detectArgumentState, PropertyConfiguration propertyConfiguration) {
        boolean isDiagnostic = detectArgumentState.isDiagnostic() || propertyConfiguration.getValueOrDefault(DetectProperties.DETECT_DIAGNOSTIC);
        boolean isExtended = detectArgumentState.isDiagnosticExtended() || propertyConfiguration.getValueOrDefault(DetectProperties.DETECT_DIAGNOSTIC_EXTENDED);
        return new DiagnosticDecision(isDiagnostic, isExtended);
    }

    private DiagnosticDecision(boolean isDiagnostic, boolean isExtended) {
        this.isDiagnostic = isDiagnostic;
        this.isExtended = isExtended;
    }

    public boolean shouldCreateDiagnosticSystem() {
        return isDiagnostic || isExtended;
    }

    public boolean isExtended() {
        return isExtended;
    }
}
