/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow;

public class ArtifactoryConstants {
    public static final String ARTIFACTORY_URL = "https://sig-repo.synopsys.com/";
    public static final String VERSION_PLACEHOLDER = "<VERSION>";

    public static final String GRADLE_INSPECTOR_MAVEN_REPO = ARTIFACTORY_URL + "bds-integration-public-cache/";

    private static final String NUGET_VERSION_SUFFIX = ".nupkg";

    public static final String NUGET_DOTNET3_INSPECTOR_REPO = "bds-integrations-nuget-release/NugetDotnet3Inspector";
    public static final String NUGET_DOTNET3_INSPECTOR_PROPERTY = "NUGET_DOTNET3_INSPECTOR_LATEST_1";
    public static final String NUGET_DOTNET3_INSPECTOR_VERSION_OVERRIDE = "/NugetDotnet3Inspector." + ArtifactoryConstants.VERSION_PLACEHOLDER + NUGET_VERSION_SUFFIX;

    public static final String NUGET_INSPECTOR_REPO = "bds-integrations-nuget-release/BlackduckNugetInspector";
    public static final String NUGET_INSPECTOR_PROPERTY = "NUGET_INSPECTOR_LATEST_1";
    public static final String NUGET_INSPECTOR_VERSION_OVERRIDE = "/BlackduckNugetInspector." + ArtifactoryConstants.VERSION_PLACEHOLDER + NUGET_VERSION_SUFFIX;

    public static final String CLASSIC_NUGET_INSPECTOR_REPO = "bds-integrations-nuget-release/IntegrationNugetInspector";
    public static final String CLASSIC_NUGET_INSPECTOR_PROPERTY = "NUGET_INSPECTOR_LATEST_3";
    public static final String CLASSIC_NUGET_INSPECTOR_VERSION_OVERRIDE = "/IntegrationNugetInspector." + ArtifactoryConstants.VERSION_PLACEHOLDER + NUGET_VERSION_SUFFIX;

    public static final String DOCKER_INSPECTOR_REPO = "bds-integrations-release/com/synopsys/integration/blackduck-docker-inspector";
    public static final String DOCKER_INSPECTOR_PROPERTY = "DOCKER_INSPECTOR_LATEST_9";
    public static final String DOCKER_INSPECTOR_AIR_GAP_PROPERTY = "DOCKER_INSPECTOR_AIR_GAP_LATEST_9";
    public static final String DOCKER_INSPECTOR_VERSION_OVERRIDE = "/" + ArtifactoryConstants.VERSION_PLACEHOLDER + "/blackduck-docker-inspector-" + ArtifactoryConstants.VERSION_PLACEHOLDER + ".jar";

    public static final String FONTS_REPO = "bds-integrations-release/com/synopsys/integration/synopsys-detect";
    public static final String FONTS_PROPERTY = "DETECT_FONT_BUNDLE_LATEST_7";
}
