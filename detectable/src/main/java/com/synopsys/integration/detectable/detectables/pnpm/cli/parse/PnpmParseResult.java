package com.synopsys.integration.detectable.detectables.pnpm.cli.parse;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;

public class PnpmParseResult {
    private String projectName;
    private String projectVersion;
    private CodeLocation codeLocation;

    public PnpmParseResult(String projectName, String projectVersion, CodeLocation codeLocation) {
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
