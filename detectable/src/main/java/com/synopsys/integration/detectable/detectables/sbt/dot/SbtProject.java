package com.synopsys.integration.detectable.detectables.sbt.dot;

public class SbtProject {
    private final String name; //ex scalafmt-dynamic
    private final String group; //ex or.scalameta
    private final String version; // ex 2.7.5-SNAPSHOT

    public SbtProject(final String name, final String group, final String version) {
        this.name = name;
        this.group = group;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public String getVersion() {
        return version;
    }
}
