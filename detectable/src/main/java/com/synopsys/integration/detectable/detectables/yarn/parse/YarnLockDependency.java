package com.synopsys.integration.detectable.detectables.yarn.parse;

public class YarnLockDependency {
    private final String name;
    private final String version;
    private final boolean optional;

    public YarnLockDependency(String name, String version, boolean optional) {
        this.name = name;
        this.version = version;
        this.optional = optional;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public boolean isOptional() {
        return optional;
    }
}
