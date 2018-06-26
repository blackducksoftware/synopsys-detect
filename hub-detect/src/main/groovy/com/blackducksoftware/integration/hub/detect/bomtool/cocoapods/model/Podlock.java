package com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.model;

import java.util.List;
import java.util.Optional;

public class Podlock {
    public List<String> rootDependencyNames;
    public List<PodlockDependency> podlockDependencies;

    public Podlock(final List<String> rootDependencyNames, final List<PodlockDependency> podlockDependencies) {
        this.rootDependencyNames = rootDependencyNames;
        this.podlockDependencies = podlockDependencies;
    }

    public Optional<PodlockDependency> getDependency(final String name) {
        return podlockDependencies.stream()
                .filter(it -> it.name.equals(name))
                .findFirst();
    }
}
