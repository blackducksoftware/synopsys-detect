package com.synopsys.integration.detectable.detectables.clang;

public class ClangDetectableOptions {
    private final boolean cleanup;

    public ClangDetectableOptions(boolean cleanup) {
        this.cleanup = cleanup;
    }

    public boolean isCleanup() {
        return cleanup;
    }
}
