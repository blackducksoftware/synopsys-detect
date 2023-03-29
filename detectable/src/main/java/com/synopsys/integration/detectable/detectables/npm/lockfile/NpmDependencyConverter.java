package com.synopsys.integration.detectable.detectables.npm.lockfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmDependency;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmProject;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmRequires;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.PackageLock;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.PackageLockPackage;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class NpmDependencyConverter {
    private final ExternalIdFactory externalIdFactory;

    public NpmDependencyConverter(ExternalIdFactory externalIdFactory) {this.externalIdFactory = externalIdFactory;}

    public NpmProject convertLockFile(PackageLock packageLock, @Nullable PackageJson packageJson) {
        List<NpmRequires> declaredDevDependencies = new ArrayList<>();
        List<NpmRequires> declaredPeerDependencies = new ArrayList<>();
        List<NpmRequires> declaredDependencies = new ArrayList<>();
        List<NpmDependency> resolvedDependencies = new ArrayList<>();

        if (packageLock.packages != null) {
            List<NpmDependency> children = convertPackageMapToDependencies(null, packageLock.packages);
            resolvedDependencies.addAll(children);
        }

        if (packageJson != null) {
            if (packageJson.dependencies != null) {
                List<NpmRequires> rootRequires = convertNameVersionMapToRequires(packageJson.dependencies);
                declaredDependencies.addAll(rootRequires);
            }

            if (packageJson.devDependencies != null) {
                List<NpmRequires> rootDevRequires = convertNameVersionMapToRequires(packageJson.devDependencies);
                declaredDevDependencies.addAll(rootDevRequires);
            }

            if (packageJson.peerDependencies != null) {
                List<NpmRequires> rootPeerRequires = convertNameVersionMapToRequires(packageJson.peerDependencies);
                declaredPeerDependencies.addAll(rootPeerRequires);
            }
        }

        return new NpmProject(packageLock.name, packageLock.version, declaredDevDependencies, declaredPeerDependencies, declaredDependencies, resolvedDependencies);
    }

    public List<NpmDependency> convertPackageMapToDependencies(NpmDependency parent, Map<String, PackageLockPackage> packages) {
        List<NpmDependency> children = new ArrayList<>();

        if (packages == null || packages.size() == 0) {
            return children;
        }

        for (Map.Entry<String, PackageLockPackage> packageEntry : packages.entrySet()) {
            String packageName = packageEntry.getKey();
            PackageLockPackage packageLockDependency = packageEntry.getValue();

            NpmDependency dependency = createNpmDependency(packageName, packageLockDependency.version, packageLockDependency.dev, packageLockDependency.peer);
            dependency.setParent(parent);
            children.add(dependency);

            List<NpmRequires> requires = convertNameVersionMapToRequires(packageLockDependency.dependencies);
            dependency.addAllRequires(requires);

            List<NpmDependency> grandChildren = convertPackageMapToDependencies(dependency, packageLockDependency.packages);
            dependency.addAllDependencies(grandChildren);
        }
        return children;
    }

    private NpmDependency createNpmDependency(String name, String version, Boolean isDev, Boolean isPeer) {
        boolean dev = isDev != null && isDev;
        boolean peer = isPeer != null && isPeer;
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, name, version);
        return new NpmDependency(name, version, externalId, dev, peer);
    }

    public List<NpmRequires> convertNameVersionMapToRequires(Map<String, String> requires) {
        if (requires == null || requires.size() == 0) {
            return Collections.emptyList();
        }
        return requires.entrySet().stream()
            .map(entry -> new NpmRequires(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    public void linkPackagesDependencies(PackageLock packageLock) {
        PackageLockPackage rootPackage = packageLock.packages.get("");
        
        Set<String> packagesToRemove = new HashSet<>();
                
        for(String packageName : packageLock.packages.keySet()) { 
            
            // Skip anything in rootPackage.workspaces, we don't want to process any custom workspace code.
            // Otherwise look for any relationships previously nested in the dependencies object prior to
            // npm 9. In the packages object these are stored at the root of the object in a parent/child and
            // perhaps even /grandchild relationship.
            if (packageName.contains("/") && !rootPackage.workspaces.contains(packageName)) { 
                
                // This packageName contains one or more /'s indicating a parent/child relationship.
                // The parent will be the portion of the package name up to and not including the final /.
                PackageLockPackage parentPackage = 
                        packageLock.packages.get(packageName.substring(0, packageName.lastIndexOf("/")));
                
                if (parentPackage.packages == null) {
                    parentPackage.packages = new HashMap<String, PackageLockPackage>();
                }
                
                // Link the child back to the parent. The child will be the leaf of the overall packageName.
                parentPackage.packages.put(packageName.substring(packageName.lastIndexOf("/") + 1, packageName.length()), 
                        packageLock.packages.get(packageName));
                
                packagesToRemove.add(packageName);
            }
        }
        
        // Now that they are processed, get rid of any packages containing a parent/child relationship. 
        // This makes the final packages structure more like the previous dependencies structure so most of the
        // detector code does not need to be altered to support npm 9 and later.
        packageLock.packages.keySet().removeAll(packagesToRemove);
    }
}
