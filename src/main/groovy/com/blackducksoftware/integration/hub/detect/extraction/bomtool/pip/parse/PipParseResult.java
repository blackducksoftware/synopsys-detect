package com.blackducksoftware.integration.hub.detect.extraction.bomtool.pip.parse;

import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

public class PipParseResult {
    public String projectName;
    public String projectVersion;
    public DetectCodeLocation codeLocation;

    public PipParseResult(final String projectName, final String projectVersion, final DetectCodeLocation codeLocation) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.codeLocation = codeLocation;
    }
}
