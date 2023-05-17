package com.synopsys.integration.detectable.detectables.npm.packagejson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
