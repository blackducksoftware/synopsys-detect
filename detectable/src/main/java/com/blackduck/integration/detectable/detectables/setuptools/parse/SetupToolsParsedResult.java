package com.blackduck.integration.detectable.detectables.setuptools.parse;

import java.util.List;

import com.blackduck.integration.detectable.python.util.PythonDependency;

public class SetupToolsParsedResult {

    private String projectName;
    private String projectVersion;
    private List<PythonDependency> directDependencies;

    public SetupToolsParsedResult(String projectName, String projectVersion, List<PythonDependency> parsedDirectDependencies) {
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

    public List<PythonDependency> getDirectDependencies() {
        return directDependencies;
    }
}
