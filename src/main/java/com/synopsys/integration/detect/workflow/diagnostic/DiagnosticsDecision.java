package com.synopsys.integration.detect.workflow.diagnostic;

public class DiagnosticsDecision {
    public final boolean isDiagnostic;
    public final boolean isDiagnosticExtended;
    public final boolean isConfiguredForDiagnostic;

    public DiagnosticsDecision(boolean isDiagnostic, boolean isDiagnosticExtended, boolean isConfiguredForDiagnostic) {
        this.isDiagnostic = isDiagnostic;
        this.isDiagnosticExtended = isDiagnosticExtended;
        this.isConfiguredForDiagnostic = isConfiguredForDiagnostic;
    }
}
