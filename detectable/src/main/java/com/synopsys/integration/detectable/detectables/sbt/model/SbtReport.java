/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.sbt.model;

import java.util.List;

public class SbtReport {
    private final String organisation;
    private final String module;
    private final String revision;
    private final String configuration;
    private final List<SbtModule> dependencies;

    public SbtReport(final String organisation, final String module, final String revision, final String configuration, final List<SbtModule> dependencies) {
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
