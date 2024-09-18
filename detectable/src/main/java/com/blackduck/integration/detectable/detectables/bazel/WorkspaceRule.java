package com.blackduck.integration.detectable.detectables.bazel;

public enum WorkspaceRule {
    MAVEN_JAR("maven_jar"),
    MAVEN_INSTALL("maven_install"),
    HASKELL_CABAL_LIBRARY("haskell_cabal_library"),
    HTTP_ARCHIVE("http_archive");

    private final String name;

    WorkspaceRule(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
