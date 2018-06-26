package com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.model;

import java.util.List;
import java.util.Optional;

import com.blackducksoftware.integration.hub.bdio.model.Forge;

public class PodlockDependency {
    public String name;
    public Optional<String> version;
    public Forge forge;
    public List<String> dependencyNames;

    public PodlockDependency(final String name, final Optional<String> version, final Forge forge, final List<String> dependencyNames) {
        this.name = name;
        this.version = version;
        this.forge = forge;
        this.dependencyNames = dependencyNames;
    }

}
