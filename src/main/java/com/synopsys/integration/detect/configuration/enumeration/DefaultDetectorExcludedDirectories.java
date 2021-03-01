/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.configuration.enumeration;

public enum DefaultDetectorExcludedDirectories {
    BIN("bin"),
    BUILD("build"),
    DOT_GRADLE(".gradle"),
    NODE_MODULES("node_modules"),
    OUT("out"),
    PACKAGES("packages"),
    GIT(".git"),
    TARGET("target");

    private final String directoryName;

    DefaultDetectorExcludedDirectories(final String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDirectoryName() {
        return directoryName;
    }

}
