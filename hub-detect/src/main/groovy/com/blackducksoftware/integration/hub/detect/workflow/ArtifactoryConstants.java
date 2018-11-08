package com.blackducksoftware.integration.hub.detect.workflow;

public class ArtifactoryConstants {
    public static String ARTIFACTORY_URL = "https://repo.blackducksoftware.com/artifactory/";
    public static String VERSION_PLACEHOLDER = "<VERSION>";

    public static String GRADLE_INSPECTOR_REPO = "bds-integrations-release/com/blackducksoftware/integration/integration-gradle-inspector";
    public static String GRADLE_INSPECTOR_PROPERTY = "GRADLE_INSPECTOR_LATEST_0";

    public static String NUGET_INSPECTOR_REPO = "bds-integrations-nuget-release/BlackduckNugetInspector";
    public static String NUGET_INSPECTOR_PROPERTY = "NUGET_INSPECTOR_LATEST_0";
    public static String NUGET_INSPECTOR_VERSION_OVERRIDE = "/BlackduckNugetInspector." + ArtifactoryConstants.VERSION_PLACEHOLDER + ".nupkg";

    public static String CLASSIC_NUGET_INSPECTOR_REPO = "bds-integrations-nuget-release/IntegrationNugetInspector";
    public static String CLASSIC_NUGET_INSPECTOR_PROPERTY = "NUGET_INSPECTOR_LATEST_0";
    public static String CLASSIC_NUGET_INSPECTOR_VERSION_OVERRIDE = "/IntegrationNugetInspector." + ArtifactoryConstants.VERSION_PLACEHOLDER + ".nupkg";

    public static String DOCKER_INSPECTOR_REPO = "bds-integrations-release/com/synopsys/integration/blackduck-docker-inspector";
    public static String DOCKER_INSPECTOR_PROPERTY = "DOCKER_INSPECTOR_LATEST_7";
    public static String DOCKER_INSPECTOR_VERSION_OVERRIDE = "/" + ArtifactoryConstants.VERSION_PLACEHOLDER + "/blackduck-docker-inspector-" + ArtifactoryConstants.VERSION_PLACEHOLDER + ".jar";

}
