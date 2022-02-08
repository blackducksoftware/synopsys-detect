package com.synopsys.integration.detectable;

import java.io.File;

public class DetectableEnvironment {
    private final File directory;

    public DetectableEnvironment(File directory) {
        this.directory = directory;
    }

    public File getDirectory() {
        return directory;
    }
}
