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

    public void setSourcePath(File sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public DependencyGraph getGraph() {
        return graph;
    }

    public void setGraph(DependencyGraph graph) {
        this.graph = graph;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }
}
