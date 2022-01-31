package com.synopsys.integration.detectable.detectables.npm.lockfile.result;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;

public class NpmPackagerResult {
    private String projectName;
    private String projectVersion;
    private CodeLocation codeLocation;

    public NpmPackagerResult(String projectName, String projectVersion, CodeLocation codeLocation) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.codeLocation = codeLocation;
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

    public CodeLocation getCodeLocation() {
        return codeLocation;
    }

    public void setCodeLocation(CodeLocation codeLocation) {
        this.codeLocation = codeLocation;
    }
}
