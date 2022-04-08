package com.synopsys.integration.detectable.detectables.swift.cli.model;

import java.util.ArrayList;
import java.util.List;

public class SwiftPackage {
    private String name;
    private String version;
    private List<SwiftPackage> dependencies = new ArrayList<>();

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

    public List<SwiftPackage> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<SwiftPackage> dependencies) {
        this.dependencies = dependencies;
    }
}
