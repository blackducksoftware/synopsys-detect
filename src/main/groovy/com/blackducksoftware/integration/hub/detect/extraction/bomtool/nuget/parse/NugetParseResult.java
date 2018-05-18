package com.blackducksoftware.integration.hub.detect.extraction.bomtool.nuget.parse;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

public class NugetParseResult {
    public String projectName;
    public String projectVersion;
    public List<DetectCodeLocation> codeLocations;

    public NugetParseResult(final String projectName, final String projectVersion, final List<DetectCodeLocation> codeLocations) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.codeLocations = codeLocations;
    }

    public NugetParseResult(final String projectName, final String projectVersion, final DetectCodeLocation codeLocation) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.codeLocations = new ArrayList<>();
        this.codeLocations.add(codeLocation);
    }
}
