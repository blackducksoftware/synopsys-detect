package com.synopsys.integration.detectable.detectables.npm.lockfile.parse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.synopsys.integration.detectable.detectables.npm.packagejson.CombinedPackageJson;
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
        CombinedPackageJson combinedPackageJson = constructCombinedPackageJson(rootJsonPath, packageJsonText);
        
        lockFileText = removePathInfoFromPackageName(lockFileText);

        PackageLock packageLock = gson.fromJson(lockFileText, PackageLock.class);
        
        NpmDependencyConverter dependencyConverter = new NpmDependencyConverter(externalIdFactory);
        
        // Link up any subpackages that no longer have a nice nested relationship in the packages areas of the 
        // lock file
        dependencyConverter.linkPackagesDependencies(packageLock);
        
        NpmProject project = dependencyConverter.convertLockFile(packageLock, combinedPackageJson);

        DependencyGraph dependencyGraph = graphTransformer.transform(packageLock, project, externalDependencies, combinedPackageJson.getWorkspaces());
        ExternalId projectId = projectIdTransformer.transform(combinedPackageJson, packageLock);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph, projectId);
        return new NpmPackagerResult(projectId.getName(), projectId.getVersion(), codeLocation);
    }

    private String removePathInfoFromPackageName(String lockFileText) {
        List<String> searchList = new ArrayList<>(Arrays.asList("/node_modules/", "node_modules/"));
        List<String> replaceList = new ArrayList<>(Arrays.asList("*", ""));

        // Flatten the lock file, removing node_modules from the package names. The code expects them in this
        // format as it aligns with the previous dependencies section of the lock file that was removed in npm9. 
        // For any package names that contain /node_modules/ not at the beginning of the path, insert a * to 
        // indicate a parent/child relationship. We'll link up later in the call to linkPackagesDependencies.
        lockFileText = StringUtils.replaceEach(lockFileText, 
                searchList.toArray(new String[searchList.size()]), 
                replaceList.toArray(new String[replaceList.size()]));
        return lockFileText;
    }

    /**
     * Merge the root package.json with any potential workspace package.json files.
     */
    private CombinedPackageJson constructCombinedPackageJson(String rootJsonPath, String packageJsonText) throws IOException {
        PackageJson packageJson = Optional.ofNullable(packageJsonText)
            .map(content -> gson.fromJson(content, PackageJson.class))
            .orElse(null);
        
        CombinedPackageJson combinedPackageJson = new CombinedPackageJson();
        
        // Take fields that will be related to BD projects from the root project.json
        combinedPackageJson.setName(packageJson.name);
        combinedPackageJson.setVersion(packageJson.version);
        combinedPackageJson.setWorkspaces(packageJson.workspaces);
        
        // Add dependencies from the root of the project
        combinedPackageJson.getDependencies().putAll(packageJson.dependencies);
        combinedPackageJson.getDevDependencies().putAll(packageJson.devDependencies);
        combinedPackageJson.getPeerDependencies().putAll(packageJson.peerDependencies);
        
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
                
                combinedPackageJson.getDependencies().putAll(workspacePackageJson.dependencies);
                combinedPackageJson.getDevDependencies().putAll(workspacePackageJson.devDependencies);
                combinedPackageJson.getPeerDependencies().putAll(workspacePackageJson.peerDependencies);
            }
        }
        
        return combinedPackageJson;
    }

}
