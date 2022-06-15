package com.synopsys.integration.detect.battery.util.executable;

import java.io.File;

public class BatteryExecutableInfo {
    private final File mockDirectory;
    private final File sourceDirectory;

    public BatteryExecutableInfo(File mockDirectory, File sourceDirectory) {
        this.mockDirectory = mockDirectory;
        this.sourceDirectory = sourceDirectory;
    }

    public File getMockDirectory() {
        return mockDirectory;
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }
}
