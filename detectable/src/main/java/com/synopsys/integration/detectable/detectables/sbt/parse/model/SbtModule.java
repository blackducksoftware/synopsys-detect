/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.sbt.parse.model;

import java.util.List;

public class SbtModule {
    private final String organisation;
    private final String name;
    private final List<SbtRevision> revisions;

    public SbtModule(final String organisation, final String name, final List<SbtRevision> revisions) {
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
