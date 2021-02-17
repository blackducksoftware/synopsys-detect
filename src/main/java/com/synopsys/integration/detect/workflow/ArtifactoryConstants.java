/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.workflow;

public class ArtifactoryConstants {
    public static final String ARTIFACTORY_URL = "https://sig-repo.synopsys.com/";
    public static final String VERSION_PLACEHOLDER = "<VERSION>";

    public static final String GRADLE_INSPECTOR_REPO = "bds-integrations-release/com/blackducksoftware/integration/integration-gradle-inspector";
    public static final String GRADLE_INSPECTOR_PROPERTY = "GRADLE_INSPECTOR_LATEST_0";
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

}
