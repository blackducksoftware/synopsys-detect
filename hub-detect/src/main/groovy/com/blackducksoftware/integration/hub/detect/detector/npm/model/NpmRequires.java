package com.blackducksoftware.integration.hub.detect.detector.npm.model;

public class NpmRequires {
    private final String name;
    private final String fuzzyVersion;

    public NpmRequires(final String name, final String fuzzyVersion) {
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