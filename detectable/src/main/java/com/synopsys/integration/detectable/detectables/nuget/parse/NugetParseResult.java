package com.synopsys.integration.detectable.detectables.nuget.parse;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;

public class NugetParseResult {
    private String projectName;
    private String projectVersion;
    private List<CodeLocation> codeLocations;

    public NugetParseResult(String projectName, String projectVersion, List<CodeLocation> codeLocations) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.codeLocations = codeLocations;
    }

    public NugetParseResult(String projectName, String projectVersion, CodeLocation codeLocation) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.codeLocations = new ArrayList<>();
        this.codeLocations.add(codeLocation);
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public List<CodeLocation> getCodeLocations() {
        return codeLocations;
    }

    public void setCodeLocations(List<CodeLocation> codeLocations) {
        this.codeLocations = codeLocations;
    }
}
