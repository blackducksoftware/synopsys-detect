package com.synopsys.integration.detectable.detectables.sbt.parse.model;

import java.util.List;

public class SbtModule {
    private final String organisation;
    private final String name;
    private final List<SbtRevision> revisions;

    public SbtModule(String organisation, String name, List<SbtRevision> revisions) {
        this.organisation = organisation;
        this.name = name;
        this.revisions = revisions;
    }

    public String getOrganisation() {
        return organisation;
    }

    public String getName() {
        return name;
    }

    public List<SbtRevision> getRevisions() {
        return revisions;
    }
}
