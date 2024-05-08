package com.synopsys.integration.detectable.detectables.setuptools.parse;

import java.util.Set;

public class SetupToolsParsedResult {

    private String projectName;
    private String projectVersion;
    private Set<String> directDependencies;

    public SetupToolsParsedResult(String projectName, String projectVersion, Set<String> directDependencies) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.directDependencies = directDependencies;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public Set<String> getDirectDependencies() {
        return directDependencies;
    }
}
