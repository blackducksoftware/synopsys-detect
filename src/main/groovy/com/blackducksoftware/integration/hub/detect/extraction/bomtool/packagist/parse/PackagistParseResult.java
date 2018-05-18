package com.blackducksoftware.integration.hub.detect.extraction.bomtool.packagist.parse;

import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

public class PackagistParseResult {
    public String projectName;
    public String projectVersion;
    public DetectCodeLocation codeLocation;

    public PackagistParseResult(final String projectName, final String projectVersion, final DetectCodeLocation codeLocation) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.codeLocation = codeLocation;
    }
}
