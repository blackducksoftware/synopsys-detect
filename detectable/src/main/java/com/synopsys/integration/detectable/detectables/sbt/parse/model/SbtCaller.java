package com.synopsys.integration.detectable.detectables.sbt.parse.model;

public class SbtCaller {
    private final String organisation;
    private final String name;
    private final String revision;

    public SbtCaller(String organisation, String name, String revision) {
        this.organisation = organisation;
        this.name = name;
        this.revision = revision;
    }

    public String getOrganisation() {
        return organisation;
    }

    public String getName() {
        return name;
    }

    public String getRevision() {
        return revision;
    }
}
