/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.synopsys.integration.detect.configuration.connection.ConnectionFactory;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;

public class ArtifactResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConnectionFactory connectionFactory;
    private final Gson gson;

    public ArtifactResolver(final ConnectionFactory connectionFactory, final Gson gson) {
        this.connectionFactory = connectionFactory;
        this.gson = gson;
    }

    /**
     * Communicates with Artifactory to find the location of an artifact.
     * Will either return the url of the given artifactory property or will calculate url the given version would point to.
     * @param artifactoryBaseUrl      The base url of artifactory, such as http://int-jfrog01.dc1.lan:8081/artifactory/
     * @param repositoryUrl           The url of the repository with the artifact, such as bds-integrations-release/com/blackducksoftware/integration/integration-gradle-inspector
     * @param propertyKey             The property to find, such as DETECT_GRADLE_INSPECTOR_LATEST_0
     * @param overrideVersion         The version to use, if provided, overrides the property tag.
     * @param overrideArtifactPattern The pattern to use when the override version is provided of the full artifact location.
     * @return the location of the artifact
     */
    public String resolveArtifactLocation(final String artifactoryBaseUrl, final String repositoryUrl, final String propertyKey, final String overrideVersion, final String overrideArtifactPattern) throws IntegrationException, IOException {
        if (StringUtils.isNotBlank(overrideVersion) && StringUtils.isNotBlank(overrideArtifactPattern)) {
            logger.debug("An override version was provided, will resolve using the given version.");
            final String repoUrl = artifactoryBaseUrl + repositoryUrl;
            final String versionUrl = overrideArtifactPattern.replace(ArtifactoryConstants.VERSION_PLACEHOLDER, overrideVersion);
            final String artifactUrl = repoUrl + versionUrl;
            logger.debug(String.format("Determined the artifact url is: %s", artifactUrl));
            return artifactUrl;
        } else {
            logger.debug("Will find version from artifactory.");
            final String apiUrl = artifactoryBaseUrl + "api/storage/" + repositoryUrl;
            logger.debug(String.format("Checking '%s' for property '%s'.", apiUrl, propertyKey));
            return downloadProperty(apiUrl, propertyKey);
        }
    }

    /**
     * Communicates with Artifactory to find the actual version of an artifact.
     * @param artifactoryBaseUrl The base url of artifactory, such as http://int-jfrog01.dc1.lan:8081/artifactory/
     * @param repositoryUrl      The url of the repository with the artifact, such as bds-integrations-release/com/blackducksoftware/integration/integration-gradle-inspector
     * @param propertyKey        The property to find, such as DETECT_GRADLE_INSPECTOR_LATEST_0
     * @return the calculated version of the artifact
     */
    public String resolveArtifactVersion(final String artifactoryBaseUrl, final String repositoryUrl, final String propertyKey) throws IntegrationException, IOException {
        logger.debug(String.format("Resolving artifact version from repository %s with property %s", repositoryUrl, propertyKey));
        final String apiUrl = artifactoryBaseUrl + "api/storage/" + repositoryUrl;
        final String artifactVersion = downloadProperty(apiUrl, propertyKey);
        logger.debug(String.format("Resolved version online: %s", artifactVersion));
        return artifactVersion;
    }

    private String downloadProperty(final String apiUrl, final String propertyKey) throws IntegrationException, IOException {
        final String propertyUrl = apiUrl + "?properties=" + propertyKey;
        logger.debug(String.format("Downloading property: %s", propertyUrl));
        final Request request = new Request.Builder().uri(propertyUrl).build();
        final IntHttpClient restConnection = connectionFactory.createConnection(propertyUrl, new SilentIntLogger());
        try (final Response response = restConnection.execute(request)) {
            try (final InputStreamReader reader = new InputStreamReader(response.getContent())) {
                logger.debug("Downloaded property, attempting to parse response.");
                final JsonObject json = gson.fromJson(reader, JsonElement.class).getAsJsonObject();
                final JsonObject propertyMap = json.getAsJsonObject("properties");
                final JsonArray propertyUrls = propertyMap.getAsJsonArray(propertyKey);
                final String foundProperty = propertyUrls.get(0).getAsString();
                logger.debug(String.format("Successfully parsed property: %s", propertyUrls));
                return foundProperty;
            }
        }
    }

    public String parseFileName(final String source) {
        final String[] pieces = source.split("/");
        return pieces[pieces.length - 1];
    }

    public File downloadOrFindArtifact(final File targetDir, final String source) throws IntegrationException, IOException {
        logger.debug("Downloading or finding artifact.");
        final String fileName = parseFileName(source);
        logger.debug(String.format("Determined filename would be: %s", fileName));
        final File fileTarget = new File(targetDir, fileName);
        logger.debug(String.format("Looking for artifact at '%s' or downloading from '%s'.", fileTarget.getAbsolutePath(), source));
        if (fileTarget.exists()) {
            logger.debug("Artifact exists. Returning existing file.");
            return fileTarget;
        } else {
            logger.debug("Artifact does not exist. Will attempt to download it.");
            return downloadArtifact(fileTarget, source);
        }
    }

    public File downloadArtifact(final File target, final String source) throws IntegrationException, IOException {
        logger.debug(String.format("Downloading for artifact to '%s' from '%s'.", target.getAbsolutePath(), source));
        final Request request = new Request.Builder().uri(source).build();
        final IntHttpClient restConnection = connectionFactory.createConnection(source, new SilentIntLogger());
        try (final Response response = restConnection.execute(request)) {
            logger.debug("Deleting existing file.");
            FileUtils.deleteQuietly(target);
            logger.debug("Writing to file.");
            final InputStream jarBytesInputStream = response.getContent();
            FileUtils.copyInputStreamToFile(jarBytesInputStream, target);
            logger.debug("Successfully wrote response to file.");
            return target;
        }
    }

}
