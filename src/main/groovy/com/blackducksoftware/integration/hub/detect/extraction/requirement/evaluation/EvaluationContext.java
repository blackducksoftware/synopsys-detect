package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation;

import java.io.File;

public class EvaluationContext {

    private final File directory;

    public EvaluationContext(final File directory) {
        this.directory = directory;
    }

    public File getDirectory() {
        return directory;
    }


}
