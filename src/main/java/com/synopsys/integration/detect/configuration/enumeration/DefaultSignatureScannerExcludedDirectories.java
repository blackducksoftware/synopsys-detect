package com.synopsys.integration.detect.configuration.enumeration;

public enum DefaultSignatureScannerExcludedDirectories {
    DOT_GRADLE(".gradle"),
    NODE_MODULES("node_modules"),
    GIT(".git"),
    SYNOPSYS(".synopsys");

    private final String directoryName;

    DefaultSignatureScannerExcludedDirectories(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDirectoryName() {
        return directoryName;
    }
}
