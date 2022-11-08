package com.synopsys.integration.detectable.detectables.pipenv.build.model;

public class PipFreezeEntry {
    private final String name;
    private final String version;

    public PipFreezeEntry(String name, String version) {
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
