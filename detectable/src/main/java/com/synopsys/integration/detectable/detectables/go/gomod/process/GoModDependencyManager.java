/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.gomod.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListAllData;
import com.synopsys.integration.detectable.detectables.go.gomod.model.ReplaceData;

public class GoModDependencyManager {
    private static final String INCOMPATIBLE_SUFFIX = "+incompatible";

    private final ExternalIdFactory externalIdFactory;

    private Map<String, Dependency> modulesAsDependencies;

    public GoModDependencyManager(List<GoListAllData> allModules, ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
        modulesAsDependencies = convertModulesToDependencies(allModules);
    }

    private Map<String, Dependency> convertModulesToDependencies(List<GoListAllData> allModules) {
        Map<String, Dependency> dependencies = new HashMap<>();

        for (GoListAllData module : allModules) {
            String name = Optional.ofNullable(module.getReplace())
                              .map(ReplaceData::getPath)
                              .orElse(module.getPath());
            String version = Optional.ofNullable(module.getReplace())
                                 .map(ReplaceData::getVersion)
                                 .orElse(module.getVersion());
            if (version != null) {
                version = handleGitHash(version);
                version = removeIncompatibleSuffix(version);
            }
            dependencies.put(module.getPath(), convertToDependency(name, version));
        }

        return dependencies;
    }

    private Dependency convertToDependency(String moduleName, @Nullable String moduleVersion) {
        return new Dependency(moduleName, moduleVersion, externalIdFactory.createNameVersionExternalId(Forge.GOLANG, moduleName, moduleVersion));
    }

    public Dependency getDependencyForModule(String moduleName) {
        return modulesAsDependencies.getOrDefault(moduleName, convertToDependency(moduleName, null));
    }

    public Optional<String> getPathForModule(String moduleName) {
        return Optional.ofNullable(modulesAsDependencies.get(moduleName)).map(Dependency::getName);
    }

    public Optional<String> getVersionForModule(String moduleName) {
        return Optional.ofNullable(modulesAsDependencies.get(moduleName)).map(Dependency::getVersion);
    }

    private String handleGitHash(String version) {
        if (version.contains("-")) { //The KB only supports the git hash, unfortunately we must strip out the rest. This gets just the commit has from a go.mod psuedo version.
            String[] versionPieces = version.split("-");
            return versionPieces[versionPieces.length - 1];
        }
        return version;
    }

    // https://golang.org/ref/mod#incompatible-versions
    private String removeIncompatibleSuffix(String version) {
        if (version.endsWith(INCOMPATIBLE_SUFFIX)) {
            // Trim incompatible suffix so that KB can match component
            version = version.substring(0, version.length() - INCOMPATIBLE_SUFFIX.length());
        }
        return version;
    }
}
