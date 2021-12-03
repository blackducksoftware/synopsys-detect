package com.synopsys.integration.detect.configuration.help;

public class DetectArgumentStateParser {

    public DetectArgumentState parseArgs(String[] args) {
        return parseArgs(new ArgumentParser(args));
    }

    public DetectArgumentState parseArgs(ArgumentParser parser) {
        boolean isHelp = parser.isArgumentPresent("-h", "--help");
        boolean isHelpJsonDocument = parser.isArgumentPresent("-hjson", "--helpjson");
        boolean isInteractive = parser.isArgumentPresent("-i", "--interactive");

        boolean isVerboseHelp = parser.isArgumentPresent("-hv", "--helpVerbose");
        boolean isDeprecatedHelp = parser.isArgumentPresent("-hd", "--helpDeprecated");

        boolean isDiagnosticProvided = parser.isArgumentPresent("-d", "--diagnostic");
        boolean isDiagnosticExtendedProvided = parser.isArgumentPresent("-de", "--diagnosticExtended");

        boolean isGenerateAirGapZip = parser.isArgumentPresent("-z", "--zip");

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
