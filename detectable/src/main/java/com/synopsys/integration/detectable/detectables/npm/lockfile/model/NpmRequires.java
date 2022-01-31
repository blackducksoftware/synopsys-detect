package com.synopsys.integration.detectable.detectables.npm.lockfile.model;

public class NpmRequires {
    private final String name;
    private final String fuzzyVersion;

    public NpmRequires(String name, String fuzzyVersion) {
        this.name = name;
        this.fuzzyVersion = fuzzyVersion;
    }

    public String getName() {
        return name;
    }

    public String getFuzzyVersion() {
        return fuzzyVersion;
    }
}