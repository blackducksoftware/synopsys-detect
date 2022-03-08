package com.synopsys.integration.detectable.extraction;

import java.io.File;

public class ExtractionEnvironment {
    private final File outputDirectory;

    public ExtractionEnvironment(File outputDirectory) {this.outputDirectory = outputDirectory;}

    public File getOutputDirectory() {
        return outputDirectory;
    }
}
