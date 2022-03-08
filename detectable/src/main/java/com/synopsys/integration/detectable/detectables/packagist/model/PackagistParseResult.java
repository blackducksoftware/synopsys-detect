package com.synopsys.integration.detectable.detectables.packagist.model;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;

public class PackagistParseResult {
    private final String projectName;
    private final String projectVersion;
    private final CodeLocation codeLocation;

    public PackagistParseResult(String projectName, String projectVersion, CodeLocation codeLocation) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.codeLocation = codeLocation;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public CodeLocation getCodeLocation() {
        return codeLocation;
    }
}
