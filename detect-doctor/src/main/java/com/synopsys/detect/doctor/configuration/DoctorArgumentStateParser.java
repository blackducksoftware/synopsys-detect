package com.synopsys.detect.doctor.configuration;

import com.blackducksoftware.integration.hub.detect.help.ArgumentParser;

public class DoctorArgumentStateParser {

    public DoctorArgumentState parseArgs(final String[] args) {
        return parseArgs(new ArgumentParser(args));
    }

    public DoctorArgumentState parseArgs(final ArgumentParser parser) {
        final boolean isExtraction = parser.isArgumentPresent("-e", "--extraction");

        return new DoctorArgumentState(isExtraction);
    }

}
