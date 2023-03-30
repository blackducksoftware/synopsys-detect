package com.synopsys.integration.detectable.detectables.npm.lockfile.parse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmDependencyConverter;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmProject;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.PackageLock;
import com.synopsys.integration.detectable.detectables.npm.lockfile.result.NpmPackagerResult;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.util.NameVersion;

public class NpmLockfilePackager {
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;
    private final NpmLockFileProjectIdTransformer projectIdTransformer;
    private final NpmLockfileGraphTransformer graphTransformer;

    public NpmLockfilePackager(Gson gson, ExternalIdFactory externalIdFactory, NpmLockFileProjectIdTransformer projectIdTransformer, NpmLockfileGraphTransformer graphTransformer) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
        this.projectIdTransformer = projectIdTransformer;
        this.graphTransformer = graphTransformer;
    }

    public NpmPackagerResult parseAndTransform(@Nullable String rootJsonPath, @Nullable String packageJsonText, String lockFileText) throws IOException {
        return parseAndTransform(rootJsonPath, packageJsonText, lockFileText, new ArrayList<>());
    }

    public NpmPackagerResult parseAndTransform(@Nullable String rootJsonPath, @Nullable String packageJsonText, String lockFileText, List<NameVersion> externalDependencies) throws IOException {
        PackageJson packageJson = constructPackageJson(rootJsonPath, packageJsonText);
          
        // Flatten the lock file, removing node_modules from the package names. The code expects them in this
        // format as it aligns with the previous dependencies section of the lock file.
        lockFileText = lockFileText.replaceAll("node_modules/", "");
        PackageLock packageLock = gson.fromJson(lockFileText, PackageLock.class);
        
        NpmDependencyConverter dependencyConverter = new NpmDependencyConverter(externalIdFactory);
        
        // Link up any subpackages that no longer have a nice nested relationship in the packages areas of the 
        // lock file
        dependencyConverter.linkPackagesDependencies(packageLock);
        
        NpmProject project = dependencyConverter.convertLockFile(packageLock, packageJson);

        DependencyGraph dependencyGraph = graphTransformer.transform(packageLock, project, externalDependencies);
        ExternalId projectId = projectIdTransformer.transform(packageJson, packageLock);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph, projectId);
        return new NpmPackagerResult(projectId.getName(), projectId.getVersion(), codeLocation);
    }

    private PackageJson constructPackageJson(String rootJsonPath, String packageJsonText) throws IOException {
        PackageJson packageJson = Optional.ofNullable(packageJsonText)
            .map(content -> gson.fromJson(content, PackageJson.class))
            .orElse(null);
        
        if (packageJson.workspaces != null && rootJsonPath != null) {
            // If there are workspaces there are additional package.json's we need to parse
            String projectRoot = rootJsonPath.substring(0, rootJsonPath.lastIndexOf("/") + 1);
            
            for(String workspace : packageJson.workspaces) {
                String workspaceJsonPath = projectRoot + workspace + "/package.json";
                
                String workspaceJsonString 
                    = FileUtils.readFileToString(new File(workspaceJsonPath), StandardCharsets.UTF_8);
                
                PackageJson workspacePackageJson = Optional.ofNullable(workspaceJsonString)
                        .map(content -> gson.fromJson(content, PackageJson.class))
                        .orElse(null);
                
                // TODO might get conflicts
                packageJson.dependencies.putAll(workspacePackageJson.dependencies);
                packageJson.devDependencies.putAll(workspacePackageJson.devDependencies);
                packageJson.peerDependencies.putAll(workspacePackageJson.peerDependencies);
            }
        }
        
        return packageJson;
    }

}
