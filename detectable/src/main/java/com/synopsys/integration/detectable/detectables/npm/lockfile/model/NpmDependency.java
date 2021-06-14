/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.npm.lockfile.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.bdio.model.dependency.Dependency;

public class NpmDependency {
    private final String name;
    private final String version;
    private final boolean devDependency;
    private final boolean peerDependency;
    private final Dependency dependency;

    public NpmDependency(String name, String version, boolean devDependency, boolean peerDependency, Dependency dependency) {
        this.name = name;
        this.version = version;
        this.devDependency = devDependency;
        this.peerDependency = peerDependency;
        this.dependency = dependency;
    }

    private NpmDependency parent;
    private final List<NpmRequires> requires = new ArrayList<>();
    private final List<NpmDependency> dependencies = new ArrayList<>();

    public Optional<NpmDependency> getParent() {
        return Optional.ofNullable(parent);
    }

    public void setParent(NpmDependency parent) {
        this.parent = parent;
    }

    public void addAllRequires(Collection<NpmRequires> required) {
        this.requires.addAll(required);
    }

    public void addAllDependencies(Collection<NpmDependency> dependencies) {
        this.dependencies.addAll(dependencies);
    }

    public List<NpmRequires> getRequires() {
        return requires;
    }

    public List<NpmDependency> getDependencies() {
        return dependencies;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public boolean isDevDependency() {
        return devDependency;
    }

    public boolean isPeerDependency() {
        return peerDependency;
    }

    public Dependency getGraphDependency() {
        return dependency;
    }
}
