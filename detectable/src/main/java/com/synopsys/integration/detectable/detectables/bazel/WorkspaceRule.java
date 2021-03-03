/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.bazel;

public enum WorkspaceRule {
    MAVEN_JAR("maven_jar"),
    MAVEN_INSTALL("maven_install"),
    HASKELL_CABAL_LIBRARY("haskell_cabal_library");

    private final String name;

    WorkspaceRule(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
