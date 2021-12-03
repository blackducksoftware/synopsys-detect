package com.synopsys.integration.detectable.detectables.sbt.parse.model;

import java.util.List;

public class SbtReport {
    private final String organisation;
    private final String module;
    private final String revision;
    private final String configuration;
    private final List<SbtModule> dependencies;

    public SbtReport(String organisation, String module, String revision, String configuration, List<SbtModule> dependencies) {
        this.organisation = organisation;
        this.module = module;
        this.revision = revision;
        this.configuration = configuration;
        this.dependencies = dependencies;
    }

    public String getOrganisation() {
        return organisation;
    }

    public String getModule() {
        return module;
    }

    public String getRevision() {
        return revision;
    }

    public String getConfiguration() {
        return configuration;
    }

    public List<SbtModule> getDependencies() {
        return dependencies;
    }
}
