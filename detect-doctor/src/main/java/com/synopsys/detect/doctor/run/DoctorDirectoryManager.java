package com.synopsys.detect.doctor.run;

import java.io.File;

public class DoctorDirectoryManager {

    public File outputDirectory;
    public File doctorDirectory;
    public File runDirectory;
    public File cacheDirectory;

    public DoctorDirectoryManager(DoctorRun doctorRun) {
        File userHome = new File(System.getProperty("user.home"));
        outputDirectory = new File(userHome, "blackduck");
        doctorDirectory = new File(outputDirectory, "doctor");
        File runsDirectory = new File(doctorDirectory, "runs");
        runDirectory = new File(runsDirectory, doctorRun.getRunId());

    }

    public File getDiagnosticOutputDirectory() {
        File diagnosticDirectory = new File(runDirectory, "diagnostic");
        diagnosticDirectory.mkdirs();
        return diagnosticDirectory;
    }

    public File getDiagnosticCacheDirectory() {
        File diagnosticDirectory = new File(doctorDirectory, "diagnostic-cache");
        diagnosticDirectory.mkdirs();
        return diagnosticDirectory;
    }
}
