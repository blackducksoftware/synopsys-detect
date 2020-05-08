package com.synopsys.integration.detectable.detectables.pip.poetry.model;

import java.util.List;

public class Package {
    private String category;
    private String description;
    private String name;
    private boolean optional;
    private List<String> python_versions;
    private String version;
    //private DependencyList dependencies;

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(final boolean optional) {
        this.optional = optional;
    }

    public List<String> getPython_versions() {
        return python_versions;
    }

    public void setPython_versions(final List<String> python_versions) {
        this.python_versions = python_versions;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    /*
    public DependencyList getDependencies() {
        return dependencies;
    }

    public void setDependencies(final DependencyList dependencies) {
        this.dependencies = dependencies;
    }

     */
}
