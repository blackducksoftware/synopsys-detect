package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm.parse;

import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

public class NpmParseResult {
    public String projectName;
    public String projectVersion;
    public DetectCodeLocation codeLocation;

    public NpmParseResult(final String projectName, final String projectVersion, final DetectCodeLocation codeLocation) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.codeLocation = codeLocation;
    }
}
