package com.synopsys.integration.detectable.detectables.pipenv.parse.model;

public class PipfileLockDependency {
    private final String name;
    private final String version;

    public PipfileLockDependency(String name, String version) {
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
