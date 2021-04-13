/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import java.util.List;
import java.util.Optional;

public class NugetLocatorOptions {
    private final List<String> packagesRepoUrl;
    private final String nugetInspectorVersion;

    public NugetLocatorOptions(List<String> packagesRepoUrl, String nugetInspectorVersion) {
        this.packagesRepoUrl = packagesRepoUrl;
        this.nugetInspectorVersion = nugetInspectorVersion;
    }

    public List<String> getPackagesRepoUrl() {
        return packagesRepoUrl;
    }

    public Optional<String> getNugetInspectorVersion() {
        return Optional.ofNullable(nugetInspectorVersion);
    }
}
