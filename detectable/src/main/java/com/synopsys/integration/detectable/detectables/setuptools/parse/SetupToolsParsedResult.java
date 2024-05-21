package com.synopsys.integration.detectable.detectables.setuptools.parse;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detectable.python.util.PythonDependency;

public class SetupToolsParsedResult {

    private String projectName;
    private String projectVersion;
    private List<PythonDependency> directDependencies;
    
    public SetupToolsParsedResult() {
        directDependencies = new ArrayList<>();
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

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }
}
