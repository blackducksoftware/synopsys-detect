package com.synopsys.integration.detectable.util;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;

public class DependencyCreator {
    private DependencyCreator() {
        // Hiding constructor
    }

    public static Dependency path(Forge forge, String path) {
        return Dependency.FACTORY.createPathDependency(forge, path);
    }

    public static Dependency moduleNames(Forge forge, String... moduleNames) {
        return Dependency.FACTORY.createModuleNamesDependency(forge, moduleNames);
    }

    public static Dependency nameVersion(Forge forge, String name, String version) {
        return Dependency.FACTORY.createNameVersionDependency(forge, name, version);
    }

    public static Dependency nameVersion(Forge forge, String name) {
        return Dependency.FACTORY.createNameVersionDependency(forge, name);
    }

    public static Dependency yocto(String layer, String name, String version) {
        return Dependency.FACTORY.createYoctoDependency(layer, name, version);
    }

    public static Dependency yocto(String layer, String name) {
        return Dependency.FACTORY.createYoctoDependency(layer, name);
    }

    public static Dependency maven(String group, String name, String version) {
        return Dependency.FACTORY.createMavenDependency(group, name, version);
    }

    public static Dependency maven(String group, String name) {
        return Dependency.FACTORY.createMavenDependency(group, name);
    }

    public static Dependency architecture(Forge forge, String name, String version, String architecture) {
        return Dependency.FACTORY.createArchitectureDependency(forge, name, version, architecture);
    }

    public static Dependency architecture(Forge forge, String name, String architecture) {
        return Dependency.FACTORY.createArchitectureDependency(forge, name, architecture);
    }
}