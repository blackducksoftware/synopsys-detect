package com.synopsys.integration.detectable.detectables.pip.parser;

public class RequirementsFileDependency {
    private final String name;
    private final String version;

    public RequirementsFileDependency(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
