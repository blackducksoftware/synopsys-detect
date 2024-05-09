package com.synopsys.integration.detectable.detectables.setuptools.parse;

import java.util.Map;

public class SetupToolsParsedResult {

    private String projectName;
    private String projectVersion;
    private Map<String, String> directDependencies;

    public SetupToolsParsedResult(String projectName, String projectVersion, Map<String, String> parsedDirectDependencies) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.directDependencies = parsedDirectDependencies;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public Map<String, String> getDirectDependencies() {
        return directDependencies;
    }
}
