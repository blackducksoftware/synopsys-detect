package com.synopsys.integration.detect.battery;

import java.io.File;

public class BatteryExecutableInfo {
    private final File mockDirectory;
    private final File sourceDirectory;

    public BatteryExecutableInfo(final File mockDirectory, final File sourceDirectory) {
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
