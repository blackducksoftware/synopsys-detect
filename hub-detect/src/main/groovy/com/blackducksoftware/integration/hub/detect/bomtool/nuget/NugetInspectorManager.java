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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigurationUtility;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.github.zafarkhaja.semver.Version;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.connection.UnauthenticatedRestConnection;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.request.Response;

public class NugetInspectorManager {
    private final Logger logger = LoggerFactory.getLogger(NugetInspectorManager.class);

    private final DetectFileManager detectFileManager;
    private final ExecutableManager executableManager;
    private final ExecutableRunner executableRunner;
    private final DetectConfiguration detectConfiguration;
    private final DetectConfigurationUtility detectConfigurationUtility;
    private final DocumentBuilder xmlDocumentBuilder;
    private final NugetXmlParser nugetXmlParser;

    private boolean hasResolvedInspector;
    private String resolvedNugetInspectorExecutable;

    public NugetInspectorManager(final DetectFileManager detectFileManager, final ExecutableManager executableManager, final ExecutableRunner executableRunner,
        final DetectConfiguration detectConfiguration, final DetectConfigurationUtility detectConfigurationUtility, final DocumentBuilder xmlDocumentBuilder,
        final NugetXmlParser nugetXmlParser) {
        this.detectFileManager = detectFileManager;
        this.executableManager = executableManager;
        this.executableRunner = executableRunner;
        this.detectConfiguration = detectConfiguration;
        this.detectConfigurationUtility = detectConfigurationUtility;
        this.xmlDocumentBuilder = xmlDocumentBuilder;
        this.nugetXmlParser = nugetXmlParser;
    }

    public String findNugetInspector() throws BomToolException {
        try {
            if (!hasResolvedInspector) {
                hasResolvedInspector = true;
                install();
            }

            return resolvedNugetInspectorExecutable;
        } catch (final Exception e) {
            throw new BomToolException(e);
        }
    }

    public void install() throws DetectUserFriendlyException, ExecutableRunnerException, IOException {
        final String nugetExecutable = executableManager
                                           .getExecutablePathOrOverride(ExecutableType.NUGET, true, new File(detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH)),
                                               detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_PATH));
        final Optional<Version> resolvedInspectorVersion = resolveInspectorVersion(nugetExecutable);
        if (resolvedInspectorVersion.isPresent()) {
            resolvedNugetInspectorExecutable = installInspector(nugetExecutable, detectFileManager.getSharedDirectory("nuget"), resolvedInspectorVersion.get().toString());
            if (resolvedNugetInspectorExecutable == null) {
                throw new DetectUserFriendlyException("Unable to install nuget inspector version from available nuget sources.", ExitCodeType.FAILURE_CONFIGURATION);
            }
        } else {
            throw new DetectUserFriendlyException("Unable to resolve nuget inspector version from available nuget sources.", ExitCodeType.FAILURE_CONFIGURATION);
        }
    }

    private Optional<Version> resolveInspectorVersion(final String nugetExecutablePath) throws ExecutableRunnerException {
        final String nugetInspectorPackageVersionRaw = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_VERSION);
        Optional<Version> version = Optional.of(Version.valueOf(nugetInspectorPackageVersionRaw));

        if (shouldUseAirGap()) {
            logger.debug("Running in airgap mode. Resolving version from local path");
            version = resolveVersionFromSource(detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_AIR_GAP_PATH), nugetExecutablePath);
        } else {
            logger.debug("Running online. Resolving version through Nuget");
            for (final String source : detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_NUGET_PACKAGES_REPO_URL)) {
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
        final File airGapNugetInspectorDirectory = new File(detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_AIR_GAP_PATH));
        return airGapNugetInspectorDirectory.exists();
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

    private Optional<Version> resolveVersionFromAPI_V2(final String nugetUrl, final String inspectorName, final String versionRange) {
        final Request request = new Request.Builder(nugetUrl).addQueryParameter("id", "'" + inspectorName + "'").build();
        List<Version> foundVersions = new ArrayList<>();

        try (final UnauthenticatedRestConnection restConnection = detectConfigurationUtility.createUnauthenticatedRestConnection(nugetUrl)) {
            final Response response = restConnection.executeRequest(request);
            final InputStream inputStream = response.getContent();
            final Document xmlDocument = xmlDocumentBuilder.parse(inputStream);
            foundVersions = nugetXmlParser.parseVersions(xmlDocument, inspectorName);
        } catch (final IOException | IntegrationException | SAXException | DetectUserFriendlyException e) {
            logger.warn(String.format("Failed to resolve nuget inspector (%s) version from url: %s", inspectorName, nugetUrl));
            logger.debug(e.getMessage(), e);
        }

        Version bestVersion = null;
        for (final Version foundVersion : foundVersions) {
            if ((bestVersion == null || foundVersion.greaterThan(bestVersion)) && foundVersion.satisfies(versionRange)) {
                bestVersion = foundVersion;
            }
        }

        return Optional.ofNullable(bestVersion);
    }

    private String installInspector(final String nugetExecutablePath, final File outputDirectory, final String inspectorVersion) throws IOException, ExecutableRunnerException {
        final File toolsDirectory;
        final String nugetInspectorName = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_NAME);

        final File airGapNugetInspectorDirectory = new File(detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_AIR_GAP_PATH));
        if (airGapNugetInspectorDirectory.exists()) {
            logger.debug("Running in airgap mode. Resolving from local path");
            toolsDirectory = new File(airGapNugetInspectorDirectory, "tools");
        } else {
            logger.debug("Running online. Resolving through nuget");

            for (final String source : detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_NUGET_PACKAGES_REPO_URL)) {
                logger.debug("Attempting source: " + source);
                final boolean success = attemptInstallInspectorFromSource(source, nugetExecutablePath, outputDirectory, inspectorVersion);
                if (success) {
                    break;
                }
            }
            final String inspectorDirectoryName = nugetInspectorName + "." + inspectorVersion;
            final File inspectorVersionDirectory = new File(outputDirectory, inspectorDirectoryName);
            toolsDirectory = new File(inspectorVersionDirectory, "tools");
        }
        final String exeName = nugetInspectorName + ".exe";
        final File inspectorExe = new File(toolsDirectory, exeName);

        if (!inspectorExe.exists()) {
            logger.warn(String.format("Could not find the %s version: %s even after an install attempt.", nugetInspectorName, inspectorVersion));
            return null;
        }

        return inspectorExe.getCanonicalPath();
    }

    private boolean attemptInstallInspectorFromSource(final String source, final String nugetExecutablePath, final File outputDirectory, final String resolvedInspectorVersion) throws IOException, ExecutableRunnerException {
        final List<String> nugetOptions = new ArrayList<>(Arrays.asList(
            "install",
            detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_NAME),
            "-OutputDirectory",
            outputDirectory.getCanonicalPath(),
            "-Source",
            source,
            "-Version",
            resolvedInspectorVersion)
        );

        final Optional<String> nugetConfigPath = detectConfiguration.getOptionalProperty(DetectProperty.DETECT_NUGET_CONFIG_PATH);
        if (nugetConfigPath.isPresent()) {
            nugetOptions.add("-ConfigFile");
            nugetOptions.add(nugetConfigPath.get());
        }

        final Executable installInspectorExecutable = new Executable(new File(detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH)), nugetExecutablePath, nugetOptions);
        final ExecutableOutput result = executableRunner.execute(installInspectorExecutable);

        return result.getReturnCode() == 0 && result.getErrorOutputAsList().size() == 0;
    }
}
