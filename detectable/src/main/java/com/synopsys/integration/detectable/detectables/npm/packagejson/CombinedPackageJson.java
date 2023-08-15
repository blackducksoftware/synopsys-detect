package com.synopsys.integration.detectable.detectables.npm.packagejson;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

public class CombinedPackageJson {
    
    private String name;
    private String version;
    private List<String> convertedWorkspaces = new ArrayList<>();
    private List<String> relativeWorkspaces = new ArrayList<>();
    
    private MultiValuedMap<String, String> dependencies;
    private MultiValuedMap<String, String> devDependencies;
    private MultiValuedMap<String, String> peerDependencies;
    
    public CombinedPackageJson() {
        dependencies = new HashSetValuedHashMap<>();
        devDependencies = new HashSetValuedHashMap<>();
        peerDependencies = new HashSetValuedHashMap<>();
    }

    public MultiValuedMap<String, String> getDependencies() {
        return dependencies;
    }

    public MultiValuedMap<String, String> getDevDependencies() {
        return devDependencies;
    }

    public MultiValuedMap<String, String> getPeerDependencies() {
        return peerDependencies;
    }
    
    public List<String> getConvertedWorkspaces() {
        return convertedWorkspaces;
    }

    public void setConvertedWorkspaces(List<String> convertedWorkspaces) {
        this.convertedWorkspaces = convertedWorkspaces;
    }
    
    public List<String> getRelativeWorkspaces() {        
        return relativeWorkspaces;
    }
    
    public void setRelativeWorkspaces(String rootJsonPath) {
        String projectRoot = rootJsonPath.substring(0, rootJsonPath.lastIndexOf("/") + 1);

        for (String absoluteWorkspacePath : convertedWorkspaces) {
            int rootIndex = absoluteWorkspacePath.indexOf(projectRoot);
            if (rootIndex != -1) {
                int packageStartIndex = rootIndex + projectRoot.length();
                if (packageStartIndex < absoluteWorkspacePath.length()) {
                    relativeWorkspaces.add(absoluteWorkspacePath.substring(packageStartIndex));
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
