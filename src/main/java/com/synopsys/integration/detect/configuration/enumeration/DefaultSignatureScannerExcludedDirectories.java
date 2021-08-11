/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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
