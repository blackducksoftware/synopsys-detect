package com.blackducksoftware.integration.hub.detect.detector.npm.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.hub.bdio.model.dependency.Dependency;

public class NpmDependency {
    private final String name;
    private final String version;
    private final boolean devDependency;
    private final Dependency dependency;

    public NpmDependency(final String name, final String version, final boolean devDependency, final Dependency dependency) {
        this.name = name;
        this.version = version;
        this.devDependency = devDependency;
        this.dependency = dependency;
    }

    private NpmDependency parent;
    private final List<NpmRequires> requires = new ArrayList<NpmRequires>();
    private final List<NpmDependency> dependencies = new ArrayList<NpmDependency>();

    public Optional<NpmDependency> getParent() {
        return Optional.ofNullable(parent);
    }

    public void setParent(final NpmDependency parent) {
        this.parent = parent;
    }

    public void addRequires(String name, String fuzzyVersion) {
        this.addRequires(new NpmRequires(name, fuzzyVersion));
    }

    public void addRequires(NpmRequires required) {
        this.requires.add(required);
    }

    public void addAllRequires(Collection<NpmRequires> required) {
        this.requires.addAll(required);
    }

    public void addDependency(NpmDependency dependency) {
        dependencies.add(dependency);
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

    public Dependency getGraphDependency() {
        return dependency;
    }
}
