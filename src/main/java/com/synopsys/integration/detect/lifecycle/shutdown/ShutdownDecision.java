/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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

    public ShutdownDecision(final @Nullable PhoneHomeManager phoneHomeManager, final @Nullable DiagnosticSystem diagnosticSystem,
        final @NotNull CleanupDecision cleanupDecision) {
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
