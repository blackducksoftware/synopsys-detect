package com.blackducksoftware.integration.hub.detect.detector.npm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.detector.npm.model.NpmDependency;
import com.blackducksoftware.integration.hub.detect.detector.npm.model.NpmRequires;
import com.blackducksoftware.integration.hub.detect.detector.npm.model.PackageJson;
import com.blackducksoftware.integration.hub.detect.detector.npm.model.PackageLock;
import com.blackducksoftware.integration.hub.detect.detector.npm.model.PackageLockDependency;
import com.synopsys.integration.hub.bdio.model.Forge;
import com.synopsys.integration.hub.bdio.model.dependency.Dependency;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class NpmDependencyConverter {
    private final ExternalIdFactory externalIdFactory;

    public NpmDependencyConverter(final ExternalIdFactory externalIdFactory) {this.externalIdFactory = externalIdFactory;}

    public NpmDependency convertLockFile(PackageLock packageLock, PackageJson packageJson) {

        NpmDependency root = createNpmDependency(packageLock.name, packageLock.version, false);

        if (packageLock.dependencies != null) {
            List<NpmDependency> children = convertPackageMapToDependencies(root, packageLock.dependencies);
            root.addAllDependencies(children);
        }

        if (packageJson.dependencies != null) {
            List<NpmRequires> rootRequires = convertNameVersionMapToRequires(packageJson.dependencies);
            root.addAllRequires(rootRequires);
        }

        if (packageJson.devDependencies != null) {
            List<NpmRequires> rootDevRequires = convertNameVersionMapToRequires(packageJson.devDependencies);
            root.addAllRequires(rootDevRequires);
        }

        return root;
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
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPM, name, version);
        Dependency graphDependency = new Dependency(name, version, externalId);
        boolean dev = false;
        if (isDev != null && isDev == true) {
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
