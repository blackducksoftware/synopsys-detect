package com.synopsys.integration.detectable.detectables.buildroot.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.util.Stringable;

public class ShowInfoComponent extends Stringable {
    private final String type;
    private final String name;
    private final String version;
    private final List<String> dependencies;

    @SerializedName("reverse_dependencies")
    private final List<String> reverseDependencies;

    public ShowInfoComponent(String type, String name, String version, List<String> dependencies, List<String> reverseDependencies) {
        this.type = type;
        this.name = name;
        this.version = version;
        this.dependencies = dependencies;
        this.reverseDependencies = reverseDependencies;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public List<String> getReverseDependencies() {
        return reverseDependencies;
    }
}
