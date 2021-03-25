package com.synopsys.integration.detect.lifecycle.boot.decision;

import java.io.File;

public class BazelData {
    private final File sourcePath;

    public BazelData(final File sourcePath) {
        this.sourcePath = sourcePath;
    }

    public File getSourcePath() {
        return sourcePath;
    }
}
