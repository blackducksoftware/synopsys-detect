package com.synopsys.integration.detectable.detectables.conan;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;

public class ConanDetectableResult {
    private final String projectName;
    private final String projectVersion;
    private final CodeLocation codeLocation;

    public ConanDetectableResult(String projectName, String projectVersion, CodeLocation codeLocation) {
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
