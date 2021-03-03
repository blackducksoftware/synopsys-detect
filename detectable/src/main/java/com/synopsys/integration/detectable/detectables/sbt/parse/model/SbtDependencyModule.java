/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.sbt.parse.model;

import java.io.File;

import com.synopsys.integration.bdio.graph.DependencyGraph;

public class SbtDependencyModule {
    private File sourcePath;
    private String name;
    private String version;
    private String org;
    private DependencyGraph graph;

    // if this is from a specific configuration
    private String configuration = null;

    public File getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(final File sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(final String org) {
        this.org = org;
    }

    public DependencyGraph getGraph() {
        return graph;
    }

    public void setGraph(final DependencyGraph graph) {
        this.graph = graph;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final String configuration) {
        this.configuration = configuration;
    }
}
