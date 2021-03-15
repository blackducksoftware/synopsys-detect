/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.configuration.help;

public class DetectArgumentStateParser {

    public DetectArgumentState parseArgs(final String[] args) {
        return parseArgs(new ArgumentParser(args));
    }

    public DetectArgumentState parseArgs(final ArgumentParser parser) {
        final boolean isHelp = parser.isArgumentPresent("-h", "--help");
        final boolean isHelpJsonDocument = parser.isArgumentPresent("-hjson", "--helpjson");
        final boolean isInteractive = parser.isArgumentPresent("-i", "--interactive");

        final boolean isVerboseHelp = parser.isArgumentPresent("-hv", "--helpVerbose");
        final boolean isDeprecatedHelp = parser.isArgumentPresent("-hd", "--helpDeprecated");

        final boolean isDiagnosticProvided = parser.isArgumentPresent("-d", "--diagnostic");
        final boolean isDiagnosticExtendedProvided = parser.isArgumentPresent("-de", "--diagnosticExtended");

        final boolean isGenerateAirGapZip = parser.isArgumentPresent("-z", "--zip");

        boolean isDiagnostic = false;
        boolean isDiagnosticExtended = false;

        if (isDiagnosticProvided || isDiagnosticExtendedProvided) {
            isDiagnostic = true;
        }
        if (isDiagnosticExtendedProvided) {
            isDiagnosticExtended = true;
        }

        String parsedValue = null;
        if (isHelp) {
            parsedValue = parser.findValueForCommand("-h", "--help");
        } else if (isGenerateAirGapZip) {
            parsedValue = parser.findValueForCommand("-z", "--zip");
        }

        return new DetectArgumentState(isHelp, isHelpJsonDocument, isInteractive, isVerboseHelp, isDeprecatedHelp, parsedValue, isDiagnostic, isDiagnosticExtended, isGenerateAirGapZip);
    }

}
