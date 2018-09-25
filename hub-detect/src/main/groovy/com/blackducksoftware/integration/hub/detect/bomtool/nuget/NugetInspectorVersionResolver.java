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
package com.blackducksoftware.integration.hub.detect.bomtool.nuget;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.blackducksoftware.integration.hub.detect.bomtool.nuget.api2.NugetXmlParser;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.api3.NugetIndexJsonParser;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.api3.NugetRegistrationJsonParser;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.api3.NugetResource;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.api3.ResourceType;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigurationUtility;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.github.zafarkhaja.semver.Version;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.connection.UnauthenticatedRestConnection;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.request.Response;

public class NugetInspectorVersionResolver {
    private final Logger logger = LoggerFactory.getLogger(NugetInspectorVersionResolver.class);

    private final ExecutableRunner executableRunner;
    private final DetectConfiguration detectConfiguration;
    private final DetectConfigurationUtility detectConfigurationUtility;
    private final DocumentBuilder xmlDocumentBuilder;
    private final NugetXmlParser nugetXmlParser;
    private final NugetRegistrationJsonParser nugetRegistrationJsonParser;
    private final NugetIndexJsonParser nugetIndexJsonParser;

    public NugetInspectorVersionResolver(final ExecutableRunner executableRunner, final DetectConfiguration detectConfiguration,
        final DetectConfigurationUtility detectConfigurationUtility, final DocumentBuilder xmlDocumentBuilder, final NugetXmlParser nugetXmlParser,
        final NugetRegistrationJsonParser nugetRegistrationJsonParser, final NugetIndexJsonParser nugetIndexJsonParser) {
        this.executableRunner = executableRunner;
        this.detectConfiguration = detectConfiguration;
        this.detectConfigurationUtility = detectConfigurationUtility;
        this.xmlDocumentBuilder = xmlDocumentBuilder;
        this.nugetXmlParser = nugetXmlParser;
        this.nugetRegistrationJsonParser = nugetRegistrationJsonParser;
        this.nugetIndexJsonParser = nugetIndexJsonParser;
    }

    public Optional<Version> resolveInspectorVersion(final String nugetExecutablePath) throws ExecutableRunnerException {
        final String nugetInspectorVersion = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_VERSION);
        final String nugetInspectorName = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_NAME);
        final String[] nugetPackageRepos = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_NUGET_PACKAGES_REPO_URL);
        Optional<Version> version = Optional.empty();

        if (shouldUseAirGap()) {
            logger.debug("Running in airgap mode. Resolving version from local path");
            version = resolveVersionFromSource(detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_AIR_GAP_PATH), nugetExecutablePath);
        }

        // Attempt to retrieve version from Nuget APIs
        if (!version.isPresent()) {
            final String inspectorName = nugetInspectorName;
            final String versionRange = nugetInspectorVersion;

            logger.debug("Running online. Resolving version through Nuget API v3");
            for (final String source : nugetPackageRepos) {
                logger.debug("Attempting source: " + source);
                Optional<Version> inspectorVersion = resolveVersionFromAPI_V3(source, inspectorName, versionRange);
                String apiVersion = "v3";

                if (!inspectorVersion.isPresent()) {
                    logger.debug("Failed to resolve version from Nuget API v3, attempting to resolve through Nuget API v2");
                    inspectorVersion = resolveVersionFromAPI_V2(source, inspectorName, versionRange);
                    apiVersion = "v2";
                }

                if (inspectorVersion.isPresent()) {
                    logger.debug(String.format("Found version [%s] in source [%s] with Nuget API %s", inspectorVersion, source, apiVersion));
                    version = inspectorVersion;
                    break;
                } else {
                    logger.debug(String.format("No version found in source [%s] matching version [%s]", source, versionRange));
                }
            }
        }

        // Version resolution via air gap or from the APIs have failed. Attempt to get a version from the nuget executable.
        if (!version.isPresent()) {
            logger.debug("Running online. Resolving version through Nuget executable");
            for (final String source : nugetPackageRepos) {
                logger.debug("Attempting source: " + source);
                final Optional<Version> inspectorVersion = resolveVersionFromSource(source, nugetExecutablePath);
                if (inspectorVersion.isPresent()) {
                    logger.debug(String.format("Found version [%s] in source [%s]", inspectorVersion, source));
                    version = inspectorVersion;
                    break;
                }
            }
        }

        return version;
    }

    private boolean shouldUseAirGap() {
        final Optional<String> airGapPath = detectConfiguration.getOptionalProperty(DetectProperty.DETECT_NUGET_INSPECTOR_AIR_GAP_PATH);
        if (airGapPath.isPresent()) {
            final File airGapNugetInspectorDirectory = new File(airGapPath.get());
            return airGapNugetInspectorDirectory.exists();
        }

        return false;
    }

    private Optional<Version> resolveVersionFromSource(final String source, final String nugetExecutablePath) throws ExecutableRunnerException {
        Optional<Version> detectVersion = Optional.empty();

        final List<String> nugetOptions = new ArrayList<>(Arrays.asList(
            "list",
            detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_NAME),
            "-Source",
            source)
        );

        final Optional<String> nugetConfigPath = detectConfiguration.getOptionalProperty(DetectProperty.DETECT_NUGET_CONFIG_PATH);
        if (nugetConfigPath.isPresent()) {
            nugetOptions.add("-ConfigFile");
            nugetOptions.add(nugetConfigPath.get());
        }

        final Executable getInspectorVersionExecutable = new Executable(new File(detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH)), nugetExecutablePath, nugetOptions);

        final List<String> output = executableRunner.execute(getInspectorVersionExecutable).getStandardOutputAsList();
        for (final String line : output) {
            final String[] lineChunks = line.split(" ");
            if (detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_NAME).equalsIgnoreCase(lineChunks[0])) {
                detectVersion = Optional.of(Version.valueOf(lineChunks[1]));
            }
        }

        return detectVersion;
    }

    private Optional<Version> resolveVersionFromAPI_V2(final String nugetPackageRepo, final String inspectorName, final String versionRange) {
        final Request request = new Request.Builder(nugetPackageRepo).addQueryParameter("id", "'" + inspectorName + "'").build();
        List<Version> foundVersions = new ArrayList<>();

        try (final UnauthenticatedRestConnection restConnection = detectConfigurationUtility.createUnauthenticatedRestConnection(nugetPackageRepo)) {
            final Response response = restConnection.executeRequest(request);
            final InputStream inputStream = response.getContent();
            final Document xmlDocument = xmlDocumentBuilder.parse(inputStream);
            foundVersions = nugetXmlParser.parseVersions(xmlDocument, inspectorName);
        } catch (final IOException | IntegrationException | SAXException | DetectUserFriendlyException e) {
            logger.warn(String.format("Failed to resolve nuget inspector (%s) version from url: %s", inspectorName, nugetPackageRepo));
            logger.debug(e.getMessage(), e);
        }

        return getBestVersion(foundVersions, versionRange);
    }

    private Optional<Version> resolveVersionFromAPI_V3(final String nugetPackageRepo, final String inspectorName, final String versionRange) {
        final List<Version> foundVersions = new ArrayList<>();
        Request request = null;

        try {
            final Optional<String> baseUrl = fetchRegistrationBaseUrl(nugetPackageRepo);
            if (baseUrl.isPresent()) {
                final URIBuilder uriBuilder = new URIBuilder(baseUrl.get());
                uriBuilder.setPath(String.format("%s/%s/index.json", uriBuilder.getPath(), inspectorName.toLowerCase()));
                request = new Request.Builder(uriBuilder.build().toString()).build();
            } else {
                throw new IntegrationException(String.format("Base URL could not be discovered from [%s]", nugetPackageRepo));
            }
        } catch (final URISyntaxException | IntegrationException e) {
            logger.warn(String.format("Failed to build uri %s/%s/index.json", nugetPackageRepo, inspectorName));
            logger.debug(e.getMessage(), e);
            return Optional.empty();
        }

        try (final UnauthenticatedRestConnection restConnection = detectConfigurationUtility.createUnauthenticatedRestConnection(nugetPackageRepo)) {
            final Response response = restConnection.executeRequest(request);
            final String jsonResponse = IOUtils.toString(response.getContent(), StandardCharsets.UTF_8);
            final List<Version> versions = nugetRegistrationJsonParser.parseNugetResponse(jsonResponse, inspectorName);

            foundVersions.addAll(versions);
        } catch (final IOException | IntegrationException | DetectUserFriendlyException e) {
            logger.warn(String.format("Failed to resolve nuget inspector (%s) version from url: %s", inspectorName, nugetPackageRepo));
            logger.debug(e.getMessage(), e);
        }

        return getBestVersion(foundVersions, versionRange);
    }

    /**
     * Fetches the base url for the Registration api from the Nuget V3 API index
     */
    private Optional<String> fetchRegistrationBaseUrl(final String nugetPackageRepo) {
        Optional<String> registrationUrl = Optional.empty();
        try (final UnauthenticatedRestConnection restConnection = detectConfigurationUtility.createUnauthenticatedRestConnection(nugetPackageRepo)) {
            final Request request = new Request.Builder(nugetPackageRepo).build();
            final Response response = restConnection.executeRequest(request);
            final String indexJson = IOUtils.toString(response.getContent(), StandardCharsets.UTF_8);
            final Optional<NugetResource> registrationBaseUrlResource = nugetIndexJsonParser.parseResourceFromIndexJson(indexJson, ResourceType.RegistrationBaseUrl);

            if (registrationBaseUrlResource.isPresent()) {
                registrationUrl = registrationBaseUrlResource.get().getId();
            }
        } catch (final IOException | IntegrationException | DetectUserFriendlyException e) {
            logger.warn(String.format("Failed to find RegistrationBaseUrl in: %s", nugetPackageRepo));
            logger.debug(e.getMessage(), e);
        }

        return registrationUrl;
    }

    private Optional<Version> getBestVersion(final List<Version> versions, final String versionRange) {
        Version bestVersion = null;
        for (final Version foundVersion : versions) {
            if ((bestVersion == null || foundVersion.greaterThan(bestVersion)) && foundVersion.satisfies(versionRange)) {
                bestVersion = foundVersion;
            }
        }

        return Optional.ofNullable(bestVersion);
    }
}
