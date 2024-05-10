package com.synopsys.integration.detectable.python.util;

public class PythonDependency {
    private final String name;
    private final String version;

    public PythonDependency(String name, String version) {
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
