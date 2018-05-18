package com.blackducksoftware.integration.hub.detect.extraction.bomtool.maven.parse;

import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

public class MavenParseResult {
    public String projectName;
    public String projectVersion;
    public DetectCodeLocation codeLocation;

    public MavenParseResult(final String projectName, final String projectVersion, final DetectCodeLocation codeLocation) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.codeLocation = codeLocation;
    }
}
