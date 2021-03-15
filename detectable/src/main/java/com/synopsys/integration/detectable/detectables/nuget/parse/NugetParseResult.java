/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.nuget.parse;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;

public class NugetParseResult {
    private String projectName;
    private String projectVersion;
    private List<CodeLocation> codeLocations;

    public NugetParseResult(final String projectName, final String projectVersion, final List<CodeLocation> codeLocations) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.codeLocations = codeLocations;
    }

    public NugetParseResult(final String projectName, final String projectVersion, final CodeLocation codeLocation) {
        this.projectName = projectName;
        this.projectVersion = projectVersion;
        this.codeLocations = new ArrayList<>();
        this.codeLocations.add(codeLocation);
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(final String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public List<CodeLocation> getCodeLocations() {
        return codeLocations;
    }

    public void setCodeLocations(final List<CodeLocation> codeLocations) {
        this.codeLocations = codeLocations;
    }
}
