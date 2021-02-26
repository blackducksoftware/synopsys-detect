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

public class NpmProject {
    private final String name;
    private final String version;

    private final List<NpmRequires> declaredDevDependencies = new ArrayList<>();
    private final List<NpmRequires> declaredDependencies = new ArrayList<>();

    private final List<NpmDependency> resolvedDependencies = new ArrayList<>();

    public NpmProject(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public void addAllDevDependencies(Collection<NpmRequires> requires) {
        this.declaredDevDependencies.addAll(requires);
    }

    public void addAllDependencies(Collection<NpmRequires> requires) {
        this.declaredDependencies.addAll(requires);
    }

    public void addAllResolvedDependencies(Collection<NpmDependency> resolvedDependencies) {
        this.resolvedDependencies.addAll(resolvedDependencies);
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public List<NpmRequires> getDeclaredDependencies() {
        return declaredDependencies;
    }

    public List<NpmRequires> getDeclaredDevDependencies() {
        return declaredDevDependencies;
    }

    public List<NpmDependency> getResolvedDependencies() {
        return resolvedDependencies;
    }
}
