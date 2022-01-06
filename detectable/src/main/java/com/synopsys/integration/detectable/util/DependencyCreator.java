package com.synopsys.integration.detectable.util;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.DependencyFactory;

public class DependencyCreator {
    public static DependencyFactory sharedFactory = new DependencyFactory(ExternalIdCreator.sharedFactory);

    public static Dependency path(final Forge forge, final String path) {
        return sharedFactory.createPathDependency(forge, path);
    }

    public static Dependency moduleNames(final Forge forge, final String... moduleNames) {
        return sharedFactory.createModuleNamesDependency(forge, moduleNames);
    }

    public static Dependency nameVersion(final Forge forge, final String name, final String version) {
        return sharedFactory.createNameVersionDependency(forge, name, version);
    }

    public static Dependency nameVersion(final Forge forge, final String name) {
        return sharedFactory.createNameVersionDependency(forge, name);
    }

    public static Dependency yocto(final String layer, final String name, final String version) {
        return sharedFactory.createYoctoDependency(layer, name, version);
    }

    public static Dependency yocto(final String layer, final String name) {
        return sharedFactory.createYoctoDependency(layer, name);
    }

    public static Dependency maven(final String group, final String name, final String version) {
        return sharedFactory.createMavenDependency(group, name, version);
    }

    public static Dependency maven(final String group, final String name) {
        return sharedFactory.createMavenDependency(group, name);
    }

    public static Dependency architecture(final Forge forge, final String name, final String version, final String architecture) {
        return sharedFactory.createArchitectureDependency(forge, name, version, architecture);
    }

    public static Dependency architecture(final Forge forge, final String name, final String architecture) {
        return sharedFactory.createArchitectureDependency(forge, name, architecture);
    }
}