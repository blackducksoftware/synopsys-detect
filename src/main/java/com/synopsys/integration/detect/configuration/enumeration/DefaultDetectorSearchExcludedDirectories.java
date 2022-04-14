package com.synopsys.integration.detect.configuration.enumeration;

public enum DefaultDetectorSearchExcludedDirectories {
    BIN("bin"),
    BUILD("build"),
    DOT_BUILD(".build"),
    DOT_GRADLE(".gradle"),
    NODE_MODULES("node_modules"),
    OUT("out"),
    PACKAGES("packages"),
    GIT(".git"),
    TARGET("target"),
    SYNOPSYS(".synopsys");

    private final String directoryName;

    DefaultDetectorSearchExcludedDirectories(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDirectoryName() {
        return directoryName;
    }

}
