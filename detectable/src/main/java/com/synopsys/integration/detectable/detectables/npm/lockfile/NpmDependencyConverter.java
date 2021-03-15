/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.npm.lockfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmDependency;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmProject;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmRequires;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.PackageLock;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.PackageLockDependency;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class NpmDependencyConverter {
    private final ExternalIdFactory externalIdFactory;

    public NpmDependencyConverter(ExternalIdFactory externalIdFactory) {this.externalIdFactory = externalIdFactory;}

    public NpmProject convertLockFile(PackageLock packageLock, @Nullable PackageJson packageJson) {
        NpmProject project = new NpmProject(packageLock.name, packageLock.version);

        if (packageLock.dependencies != null) {
            List<NpmDependency> children = convertPackageMapToDependencies(null, packageLock.dependencies);
            project.addAllResolvedDependencies(children);
        }

        if (packageJson != null) {
            if (packageJson.dependencies != null) {
                List<NpmRequires> rootRequires = convertNameVersionMapToRequires(packageJson.dependencies);
                project.addAllDependencies(rootRequires);
            }

            if (packageJson.devDependencies != null) {
                List<NpmRequires> rootDevRequires = convertNameVersionMapToRequires(packageJson.devDependencies);
                project.addAllDevDependencies(rootDevRequires);
            }
        }

        return project;
    }

    public List<NpmDependency> convertPackageMapToDependencies(NpmDependency parent, Map<String, PackageLockDependency> packageLockDependencyMap) {
        List<NpmDependency> children = new ArrayList<>();

        if (packageLockDependencyMap == null || packageLockDependencyMap.size() == 0) {
            return children;
        }

        for (Map.Entry<String, PackageLockDependency> packageEntry : packageLockDependencyMap.entrySet()) {
            String packageName = packageEntry.getKey();
            PackageLockDependency packageLockDependency = packageEntry.getValue();

            NpmDependency dependency = createNpmDependency(packageName, packageLockDependency.version, packageLockDependency.dev);
            dependency.setParent(parent);
            children.add(dependency);

            List<NpmRequires> requires = convertNameVersionMapToRequires(packageLockDependency.requires);
            dependency.addAllRequires(requires);

            List<NpmDependency> grandChildren = convertPackageMapToDependencies(dependency, packageLockDependency.dependencies);
            dependency.addAllDependencies(grandChildren);
        }
        return children;
    }

    private NpmDependency createNpmDependency(String name, String version, Boolean isDev) {
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, name, version);
        Dependency graphDependency = new Dependency(name, version, externalId);
        boolean dev = false;
        if (isDev != null && isDev) {
            dev = true;
        }
        return new NpmDependency(name, version, dev, graphDependency);

    }

    public List<NpmRequires> convertNameVersionMapToRequires(Map<String, String> requires) {
        if (requires == null || requires.size() == 0) {
            return Collections.emptyList();
        }
        return requires.entrySet().stream()
                   .map(entry -> new NpmRequires(entry.getKey(), entry.getValue()))
                   .collect(Collectors.toList());
    }

}
