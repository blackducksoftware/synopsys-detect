package com.synopsys.integration.detectable.detectables.npm.lockfile.parse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
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
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmDependency;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmProject;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.PackageLock;
import com.synopsys.integration.detectable.detectables.npm.lockfile.result.NpmPackagerResult;
import com.synopsys.integration.detectable.detectables.npm.packagejson.CombinedPackageJson;
import com.synopsys.integration.detectable.detectables.npm.packagejson.CombinedPackageJsonExtractor;
import com.synopsys.integration.detectable.detectables.npm.packagejson.PackageJsonExtractor;
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
        CombinedPackageJsonExtractor extractor = new CombinedPackageJsonExtractor(gson);
        CombinedPackageJson combinedPackageJson = extractor.constructCombinedPackageJson(rootJsonPath, packageJsonText);
        
        lockFileText = removePathInfoFromPackageName(lockFileText);

        PackageLock packageLock = gson.fromJson(lockFileText, PackageLock.class);
        
        NpmDependencyConverter dependencyConverter = new NpmDependencyConverter(externalIdFactory);
        
        // Link up any subpackages that no longer have a nice nested relationship in the packages areas of the 
        // lock file
        dependencyConverter.linkPackagesDependencies(packageLock);
        
        NpmProject project = dependencyConverter.convertLockFile(packageLock, combinedPackageJson);
        
//        for (NpmDependency dependency : project.getResolvedDependencies()) {
//            //System.out.println(dependency.getName());
//            // TODO go after child dependencies until run out of them then go after requires
//            List<NpmDependency> dependencies = dependency.getDependencies();
//            Collections.sort(dependencies, new DependencyComparator());
//            for (NpmDependency childDependency1 : dependencies) {
//               // System.out.println(childDependency1.getName());
//                List<NpmDependency> child1Dependencies = childDependency1.getDependencies();
//                Collections.sort(child1Dependencies, new DependencyComparator());
//                for (NpmDependency childDependency2: child1Dependencies) {
//                    System.out.println(childDependency2.getName());
//                }
//            }
//        }
        
        DependencyGraph dependencyGraph = graphTransformer.transform(packageLock, project, externalDependencies, 
                combinedPackageJson == null ? null : combinedPackageJson.getRelativeWorkspaces());
        ExternalId projectId = projectIdTransformer.transform(combinedPackageJson, packageLock);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph, projectId);
        return new NpmPackagerResult(projectId.getName(), projectId.getVersion(), codeLocation);
    }

    public String removePathInfoFromPackageName(String lockFileText) {
        List<String> searchList = new LinkedList<>();
        List<String> replaceList = new LinkedList<>();
        
        // If we have workspaces, make sure to strip those off as well
        //"packages/react-components/node_modules/@mdx-js/mdx/node_modules/@babel/core"
//        if (rootJsonPath != null && absoluteWorkpacePaths != null && absoluteWorkpacePaths.size() > 0) {
//            String projectRoot = rootJsonPath.substring(0, rootJsonPath.lastIndexOf("/") + 1); 
//            
//            for (String absoluteWorkspacePath : absoluteWorkpacePaths) {
//                int rootIndex = absoluteWorkspacePath.indexOf(projectRoot);
//                if (rootIndex != -1) {
//                    int packageStartIndex = rootIndex + projectRoot.length();
//                    if (packageStartIndex < absoluteWorkspacePath.length()) {
//                        searchList.add(absoluteWorkspacePath.substring(packageStartIndex) + "/node_modules/");
//                        replaceList.add("");
//                    }
//                }
//            }
//        }
        
        searchList.add("node_modules/");
        replaceList.add("");
        searchList.add("/node_modules/");
        replaceList.add("*");

        // Flatten the lock file, removing node_modules from the package names. The code expects them in this
        // format as it aligns with the previous dependencies section of the lock file that was removed in npm9. 
        // For any package names that contain /node_modules/ not at the beginning of the path, insert a * to 
        // indicate a parent/child relationship. We'll link up later in the call to linkPackagesDependencies.
        lockFileText = StringUtils.replaceEach(lockFileText, 
                searchList.toArray(new String[searchList.size()]), 
                replaceList.toArray(new String[replaceList.size()]));
        return lockFileText;
    }
    
    class DependencyComparator implements Comparator<NpmDependency> {

        @Override
        public int compare(NpmDependency o1, NpmDependency o2) {
                NpmDependency one = (NpmDependency) o1;
                NpmDependency two = (NpmDependency) o2;
                // TODO Auto-generated method stub
                return one.getName().compareTo(two.getName());
        }
        
    }
}
