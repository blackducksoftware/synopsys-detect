/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.sbt.model;

public class SbtCaller {
    private final String organisation;
    private final String name;
    private final String revision;

    public SbtCaller(final String organisation, final String name, final String revision) {
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
