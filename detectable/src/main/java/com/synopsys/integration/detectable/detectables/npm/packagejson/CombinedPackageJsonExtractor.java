package com.synopsys.integration.detectable.detectables.npm.packagejson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class CombinedPackageJsonExtractor {
    
    Gson gson;
    
    public CombinedPackageJsonExtractor(Gson gson) {
        this.gson = gson;
    }
    
    /**
     * Merge the root package.json with any potential workspace package.json files.
     */
    public CombinedPackageJson constructCombinedPackageJson(String rootJsonPath, String packageJsonText) throws IOException {
        if (packageJsonText == null) {
            return null;
        }
        
        PackageJson packageJson = Optional.ofNullable(packageJsonText)
            .map(content -> gson.fromJson(content, PackageJson.class))
            .orElse(null);
        
        if (packageJson == null) {
            return null;
        }
        
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
            
            List<String> convertedWorkspaces = 
                    convertWorkspaceWildcards(projectRoot, packageJson.workspaces);
            
            for(String convertedWorkspace : convertedWorkspaces) {
                Path workspaceJsonPath =
                        Path.of(convertedWorkspace + "/package.json").normalize();
                
                // We are looking for a package.json but they aren't always where we expect them.
                // Don't try to read a file that doesn't exist.
                if (!Files.exists(workspaceJsonPath)) {
                    continue;
                } else {
                    combinedPackageJson.getConvertedWorkspaces().add(convertedWorkspace);
                }
                
                String workspaceJsonString 
                    = FileUtils.readFileToString(new File(workspaceJsonPath.toString()), StandardCharsets.UTF_8);
                
                PackageJson workspacePackageJson = Optional.ofNullable(workspaceJsonString)
                        .map(content -> gson.fromJson(content, PackageJson.class))
                        .orElse(null);
                
                if (workspacePackageJson != null) {
                    combinedPackageJson.getDependencies().putAll(workspacePackageJson.dependencies);
                    combinedPackageJson.getDevDependencies().putAll(workspacePackageJson.devDependencies);
                    combinedPackageJson.getPeerDependencies().putAll(workspacePackageJson.peerDependencies);
                }
            }
            // TODO maybe just carry this and get rid of the absolute one but there are a few callers to check
            combinedPackageJson.setRelativeWorkspaces(rootJsonPath);
        }
        
        return combinedPackageJson;
    }

    /**
     * This method searches the filesystem and replaces any * in the package.json's workspace object with
     * actual paths.
     * 
     * @param projectRoot The path to the root of the npm project
     * @param workspaces The contents of the package.json's workspace object
     * @return
     */
    private List<String> convertWorkspaceWildcards(String projectRoot, List<String> workspaces) {
        List<String> convertedWorkspaces = new ArrayList<>();
        
        for (String workspace : workspaces) {
            replaceWildcards(projectRoot + workspace, convertedWorkspaces);
        }                
        
        return convertedWorkspaces;
    }
    
    private String replaceWildcards(String path, List<String> convertedWorkspaces) {        
        if (!path.contains("*")) {
            convertedWorkspaces.add(Path.of(path).normalize().toString());
            return path;
        }
        
        int wildcardIndex = path.indexOf("*");
        String prefix = path.substring(0, wildcardIndex);
        String suffix = path.substring(wildcardIndex + 1);
        
        File[] files = new File(prefix).listFiles(File::isDirectory);
        String newPath = "";
        
        if (files != null) {
            for (File file: files) {
                // Handle any weird relative path pieces, such as ./ before turning
                // into an absolute path.
                newPath = file.toPath().normalize().toFile().getAbsolutePath() + suffix;
                if (newPath.contains("*")) {
                    newPath = replaceWildcards(newPath, convertedWorkspaces);
                } else {
                    convertedWorkspaces.add(newPath);
                }
            }
        }
        
        return newPath;
    }
}
