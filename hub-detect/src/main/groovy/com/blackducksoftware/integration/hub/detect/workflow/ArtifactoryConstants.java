/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.workflow;

public class ArtifactoryConstants {
    public static String ARTIFACTORY_URL = "https://repo.blackducksoftware.com/artifactory/";
    public static String VERSION_PLACEHOLDER = "<VERSION>";

    public static String GRADLE_INSPECTOR_REPO = "bds-integrations-release/com/blackducksoftware/integration/integration-gradle-inspector";
    public static String GRADLE_INSPECTOR_PROPERTY = "GRADLE_INSPECTOR_LATEST_0";
    public static String GRADLE_INSPECTOR_MAVEN_REPO = "https://repo.blackducksoftware.com/artifactory/bds-integration-public-cache/";

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
