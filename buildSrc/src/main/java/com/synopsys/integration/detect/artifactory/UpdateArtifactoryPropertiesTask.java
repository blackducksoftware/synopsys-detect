/*
 * buildSrc
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
package com.synopsys.integration.detect.artifactory;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import com.synopsys.integration.Common;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.rest.HttpMethod;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.body.BodyContent;
import com.synopsys.integration.rest.body.StringBodyContent;
import com.synopsys.integration.rest.client.AuthenticatingIntHttpClient;
import com.synopsys.integration.rest.client.BasicAuthHttpClient;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.rest.support.AuthenticationSupport;

public class UpdateArtifactoryPropertiesTask extends DefaultTask {
    private final IntLogger logger = new Slf4jIntLogger(getLogger());
    private final Project project = this.getProject();

    @TaskAction
    public void updateArtifactoryProperties() {
        String projectName = project.getName();
        String projectVersion = project.getVersion().toString();
        boolean isSnapshot = StringUtils.endsWith(projectVersion, "-SNAPSHOT");

        if (isSnapshot || "true".equals(project.findProperty("qa.build"))) {
            logger.alwaysLog("For a snapshot or qa build, artifactory properties will not be updated.");
            return;
        }

        logger.alwaysLog("For a release build, an update of artifactory properties will be attempted.");

        String artifactoryDeployerUsername = getExtensionProperty(Common.PROPERTY_ARTIFACTORY_DEPLOYER_USERNAME);
        String artifactoryDeployerPassword = getExtensionProperty(Common.PROPERTY_ARTIFACTORY_DEPLOYER_PASSWORD);
        String artifactoryDeploymentUrl = getExtensionProperty(Common.PROPERTY_DEPLOY_ARTIFACTORY_URL);
        String artifactoryRepository = getExtensionProperty(Common.PROPERTY_ARTIFACTORY_REPO);
        String artifactoryDownloadUrl = getExtensionProperty(Common.PROPERTY_DOWNLOAD_ARTIFACTORY_URL);

        AuthenticatingIntHttpClient httpClient = new BasicAuthHttpClient(logger, 200, true, ProxyInfo.NO_PROXY_INFO, new AuthenticationSupport(), artifactoryDeployerUsername, artifactoryDeployerPassword);

        String majorVersion = projectVersion.split("\\.")[0];
        String latestPropertyKey = "DETECT_LATEST";
        String majorVersionPropertyKey = String.format("%s_%s", latestPropertyKey, majorVersion);
        String constructedDownloadUri = String.format("%s/%s/com/synopsys/integration/%s/%s/%s-%s.jar", artifactoryDownloadUrl, artifactoryRepository, projectName, projectVersion, projectName, projectVersion);

        if (constructedDownloadUri.contains("internal")) {
            logger.error(String.format("Failing due to url containing \"internal\". URL: %s", constructedDownloadUri));
            logger.alwaysLog(String.format("Manual corrections to the properties for %s-%s may be necessary.", projectName, projectVersion));
            return;
        }

        try {
            setArtifactoryProperty(httpClient, artifactoryDeploymentUrl, artifactoryRepository, latestPropertyKey, constructedDownloadUri);
            setArtifactoryProperty(httpClient, artifactoryDeploymentUrl, artifactoryRepository, majorVersionPropertyKey, constructedDownloadUri);
        } catch (IntegrationException | IOException e) {
            logger.alwaysLog(String.format("Manual corrections to the properties for %s-%s may be necessary.", projectName, projectVersion));
            logger.error(String.format("Error setting the artifactory properties: %s", e.getMessage()), e);
        }
    }

    private String getExtensionProperty(String propertyName) {
        return Optional.ofNullable(project.findProperty(propertyName))
                   .map(Object::toString)
                   .orElseThrow(() -> new IllegalArgumentException(String.format("Missing Gradle extension property '%s' which is required to set Artifactory properties.", propertyName)));
    }

    private void setArtifactoryProperty(IntHttpClient httpClient, String artifactoryDeploymentUrl, String deploymentRepositoryKey, String propertyKey, String propertyValue)
        throws IntegrationException, IOException {
        HttpUrl baseUrl = new HttpUrl(String.format("%s/api/metadata/%s/com/synopsys/integration/%s", artifactoryDeploymentUrl, deploymentRepositoryKey, project.getName()));
        BodyContent bodyContent = new StringBodyContent(String.format("{\"props\":{\"%s\":\"%s\"}}", propertyKey, propertyValue));
        Request request = new Request.Builder()
                              .url(baseUrl)
                              .method(HttpMethod.PATCH)
                              .acceptMimeType("application/json")
                              .bodyContent(bodyContent)
                              .build();

        try (Response response = httpClient.execute(request)) {
            if (response.isStatusCodeSuccess()) {
                logger.alwaysLog(String.format("Successfully set Artifactory property '%s' to '%s'.", propertyKey, propertyValue));
            } else {
                logger.error(String.format("Failed to update Artifactory property '%s'.", propertyKey));
                logger.error(response.getContentString());
            }
        }
    }
}
