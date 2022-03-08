package com.synopsys.integration.detect.lifecycle.shutdown;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;

public class ShutdownDecision {
    @Nullable
    private final PhoneHomeManager phoneHomeManager; //If provided, will be ended.
    @Nullable
    private final DiagnosticSystem diagnosticSystem; //If provided, will be finished.
    @NotNull
    private final CleanupDecision cleanupDecision;

    public ShutdownDecision(@Nullable PhoneHomeManager phoneHomeManager, @Nullable DiagnosticSystem diagnosticSystem, @NotNull CleanupDecision cleanupDecision) {
        this.phoneHomeManager = phoneHomeManager;
        this.diagnosticSystem = diagnosticSystem;
        this.cleanupDecision = cleanupDecision;
    }

    public PhoneHomeManager getPhoneHomeManager() {
        return phoneHomeManager;
    }

    public DiagnosticSystem getDiagnosticSystem() {
        return diagnosticSystem;
    }

    public CleanupDecision getCleanupDecision() {
        return cleanupDecision;
    }
}
