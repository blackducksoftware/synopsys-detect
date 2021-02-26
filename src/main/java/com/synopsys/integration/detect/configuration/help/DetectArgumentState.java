/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.configuration.help;

import org.jetbrains.annotations.Nullable;

public class DetectArgumentState {
    private final boolean isHelp;
    private final boolean isHelpJsonDocument;
    private final boolean isInteractive;

    private final boolean isVerboseHelp;
    private final boolean isDeprecatedHelp;
    @Nullable
    private final String parsedValue;

    private final boolean isDiagnostic;
    private final boolean isDiagnosticExtended;

    private final boolean isGenerateAirGapZip;

    public DetectArgumentState(boolean isHelp, boolean isHelpJsonDocument, boolean isInteractive, boolean isVerboseHelp, boolean isDeprecatedHelp, @Nullable String parsedValue, boolean isDiagnostic, boolean isDiagnosticExtended,
        boolean isGenerateAirGapZip) {
        this.isHelp = isHelp;
        this.isHelpJsonDocument = isHelpJsonDocument;
        this.isInteractive = isInteractive;
        this.isVerboseHelp = isVerboseHelp;
        this.isDeprecatedHelp = isDeprecatedHelp;
        this.parsedValue = parsedValue;
        this.isDiagnostic = isDiagnostic;
        this.isDiagnosticExtended = isDiagnosticExtended;
        this.isGenerateAirGapZip = isGenerateAirGapZip;
    }

    public boolean isHelp() {
        return isHelp;
    }

    public boolean isHelpJsonDocument() {
        return isHelpJsonDocument;
    }

    public boolean isInteractive() {
        return isInteractive;
    }

    public boolean isVerboseHelp() {
        return isVerboseHelp;
    }

    public boolean isDeprecatedHelp() {
        return isDeprecatedHelp;
    }

    public boolean isDiagnostic() {
        return isDiagnostic;
    }

    public boolean isDiagnosticExtended() {
        return isDiagnosticExtended;
    }

    @Nullable
    public String getParsedValue() {
        return parsedValue;
    }

    public boolean isGenerateAirGapZip() {
        return isGenerateAirGapZip;
    }
}
