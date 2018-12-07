package com.synopsys.detect.doctor.diagnosticparser;

import java.io.File;

public class DetectRunInfo {

    private File extractionsFolder;
    private File logFile;

    public DetectRunInfo(final File logFile, File extractionsFolder) {
        this.extractionsFolder = extractionsFolder;
        this.logFile = logFile;
    }

    public File getLogFile() {
        return logFile;
    }

    public File getExtractionsFolder() {
        return extractionsFolder;
    }
}
