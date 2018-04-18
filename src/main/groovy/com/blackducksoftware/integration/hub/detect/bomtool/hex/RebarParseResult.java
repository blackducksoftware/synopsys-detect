package com.blackducksoftware.integration.hub.detect.bomtool.hex;

import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

public class RebarParseResult {
    public String projectName;
    public String projectVersion;
    public DetectCodeLocation codeLocation;

    public RebarParseResult(final String projectName, final String projectVersion, final DetectCodeLocation codeLocation) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.codeLocation = codeLocation;
    }
}
