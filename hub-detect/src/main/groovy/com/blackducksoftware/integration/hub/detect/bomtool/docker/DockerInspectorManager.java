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
package com.blackducksoftware.integration.hub.detect.bomtool.docker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.rest.connection.UnauthenticatedRestConnection;
import com.blackducksoftware.integration.rest.request.Request;
import com.blackducksoftware.integration.rest.request.Response;
import com.blackducksoftware.integration.util.ResourceUtil;

public class DockerInspectorManager {
    private static final String LATEST_URL = "https://blackducksoftware.github.io/hub-docker-inspector/hub-docker-inspector.sh";
    private final Logger logger = LoggerFactory.getLogger(DockerInspectorManager.class);

    private final String dockerSharedDirectoryName = "docker";

    private final DetectFileManager detectFileManager;
    private final ExecutableManager executableManager;
    private final ExecutableRunner executableRunner;
    private final DetectConfiguration detectConfiguration;
    private final DetectConfigurationUtility detectConfigurationUtility;

    public DockerInspectorManager(final DetectFileManager detectFileManager, final ExecutableManager executableManager, final ExecutableRunner executableRunner,
            final DetectConfiguration detectConfiguration, final DetectConfigurationUtility detectConfigurationUtility) {
        this.detectFileManager = detectFileManager;
        this.executableManager = executableManager;
        this.executableRunner = executableRunner;
        this.detectConfiguration = detectConfiguration;
        this.detectConfigurationUtility = detectConfigurationUtility;
    }

    private boolean hasResolvedInspector;
    private DockerInspectorInfo resolvedInfo;

    public DockerInspectorInfo getDockerInspector() throws BomToolException {
        try {
            if (!hasResolvedInspector) {
                install();
            }
            return resolvedInfo;
        } catch (final Exception e) {
            throw new BomToolException(e);
        }
    }

    private void install() throws DetectUserFriendlyException {
        hasResolvedInspector = true;

        final DockerInspectorInfo info = resolveShellScript();
        final String bashExecutablePath = executableManager
                .getExecutablePathOrOverride(ExecutableType.BASH, true, new File(detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH)), detectConfiguration.getProperty(DetectProperty.DETECT_BASH_PATH));
        info.version = resolveInspectorVersion(bashExecutablePath, info.dockerInspectorScript);

        if (info.isOffline) {
            final String dockerInspectorAirGapPath = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH);
            final String inspectorImageFamily = resolveInspectorImageFamily(bashExecutablePath, info.dockerInspectorScript);
            info.offlineDockerInspectorJar = new File(dockerInspectorAirGapPath, "hub-docker-inspector-" + info.version + ".jar");
            for (final String os : Arrays.asList("ubuntu", "alpine", "centos")) {
                final File osImage = new File(dockerInspectorAirGapPath, inspectorImageFamily + "-" + os + ".tar");
                info.offlineTars.add(osImage);
            }
        }

        resolvedInfo = info;
    }

    private String resolveInspectorImageFamily(final String bashExecutablePath, final File dockerInspectorShellScript) throws DetectUserFriendlyException {
        try {
            final String dockerInspectorArg = "--inspectorimagefamily";
            final String inspectorImageFamily = getResponseFromDockerInspector(bashExecutablePath, dockerInspectorShellScript, dockerInspectorArg);
            logger.info(String.format("Resolved docker inspector image family to: %s", inspectorImageFamily));
            return inspectorImageFamily;
        } catch (final Exception e) {
            throw new DetectUserFriendlyException("Unable to find docker inspector version.", e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }

    private String resolveInspectorVersion(final String bashExecutablePath, final File dockerInspectorShellScript) throws DetectUserFriendlyException {
        try {
            final String dockerInspectorVersion = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_VERSION);
            if ("latest".equalsIgnoreCase(dockerInspectorVersion)) {
                final String dockerInspectorArg = "--version";
                final String responseFromDockerInspector = getResponseFromDockerInspector(bashExecutablePath, dockerInspectorShellScript, dockerInspectorArg);
                final String inspectorVersion = responseFromDockerInspector.split(" ")[1];
                logger.info(String.format("Resolved docker inspector version from latest to: %s", inspectorVersion));
                return inspectorVersion;
            } else {
                return dockerInspectorVersion;
            }
        } catch (final Exception e) {
            throw new DetectUserFriendlyException("Unable to find docker inspector version.", e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }

    private String getResponseFromDockerInspector(final String bashExecutablePath, final File dockerInspectorShellScript, final String dockerInspectorArg) throws IOException, ExecutableRunnerException {
        final File inspectorDirectory = detectFileManager.getSharedDirectory(dockerSharedDirectoryName);
        final List<String> bashArguments = new ArrayList<>();
        bashArguments.add("-c");
        bashArguments.add("\"" + dockerInspectorShellScript.getCanonicalPath() + "\" " + dockerInspectorArg);
        final Executable dockerInspectorExecutable = new Executable(inspectorDirectory, bashExecutablePath, bashArguments);
        final String responseFromDockerInspector = executableRunner.execute(dockerInspectorExecutable).getStandardOutput();
        return responseFromDockerInspector;
    }

    private DockerInspectorInfo resolveShellScript() throws DetectUserFriendlyException {
        try {
            final String suppliedDockerVersion = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_VERSION);
            final File shellScriptFile;
            final File airGapHubDockerInspectorShellScript = new File(detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH), "hub-docker-inspector.sh");
            boolean isOffline = false;
            logger.debug(String.format("Verifying air gap shell script present at %s", airGapHubDockerInspectorShellScript.getCanonicalPath()));

            final String dockerInspectorPath = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_PATH);
            if (StringUtils.isNotBlank(dockerInspectorPath)) {
                shellScriptFile = new File(dockerInspectorPath);
            } else if (airGapHubDockerInspectorShellScript.exists()) {
                shellScriptFile = airGapHubDockerInspectorShellScript;
                isOffline = true;
            } else {
                final String dockerInspectorVersion = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_VERSION);
                String hubDockerInspectorShellScriptUrl = LATEST_URL;
                if (!"latest".equals(dockerInspectorVersion)) {
                    hubDockerInspectorShellScriptUrl = String.format("https://blackducksoftware.github.io/hub-docker-inspector/hub-docker-inspector-%s.sh", dockerInspectorVersion);
                }
                logger.info(String.format("Getting the Docker inspector shell script from %s", hubDockerInspectorShellScriptUrl));

                final Request request = new Request.Builder().uri(hubDockerInspectorShellScriptUrl).build();
                String shellScriptContents;
                Response response = null;

                try (UnauthenticatedRestConnection restConnection = detectConfigurationUtility.createUnauthenticatedRestConnection(hubDockerInspectorShellScriptUrl)) {
                    response = restConnection.executeRequest(request);
                    shellScriptContents = response.getContentString();
                } finally {
                    ResourceUtil.closeQuietly(response);
                }

                final File inspectorDirectory = detectFileManager.getSharedDirectory(dockerSharedDirectoryName);
                shellScriptFile = new File(inspectorDirectory, String.format("hub-docker-inspector-%s.sh", suppliedDockerVersion));
                detectFileManager.writeToFile(shellScriptFile, shellScriptContents);
                if (!shellScriptFile.setExecutable(true)) {
                    throw new DetectUserFriendlyException(String.format("The User does not have permission to execute the docker inspector shell script: %s", shellScriptFile.getAbsolutePath()), ExitCodeType.FAILURE_GENERAL_ERROR);
                }
            }

            final DockerInspectorInfo info = new DockerInspectorInfo();
            info.dockerInspectorScript = shellScriptFile;
            info.isOffline = isOffline;

            return info;
        } catch (final Exception e) {
            throw new DetectUserFriendlyException(String.format("There was a problem retrieving the docker inspector shell script: %s", e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }
}
