package com.synopsys.integration.detect.workflow.diagnostic;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.help.DetectArgumentState;

public class DiagnosticsDecider {
    private final DetectArgumentState detectArgumentState;
    private final PropertyConfiguration propertyConfiguration;

    public DiagnosticsDecider(DetectArgumentState detectArgumentState, PropertyConfiguration propertyConfiguration) {
        this.detectArgumentState = detectArgumentState;
        this.propertyConfiguration = propertyConfiguration;
    }

    public boolean isDiagnostic() {
        return detectArgumentState.isDiagnostic() || propertyConfiguration.getValueOrDefault(DetectProperties.Companion.getDETECT_DIAGNOSTIC());
    }

    public boolean isDiagnosticExtended() {
        return detectArgumentState.isDiagnosticExtended() || propertyConfiguration.getValueOrDefault(DetectProperties.Companion.getDETECT_DIAGNOSTIC_EXTENDED());
    }

    public boolean isConfiguredForDiagnostic() {
        return isDiagnostic() || isDiagnosticExtended();
    }
}
