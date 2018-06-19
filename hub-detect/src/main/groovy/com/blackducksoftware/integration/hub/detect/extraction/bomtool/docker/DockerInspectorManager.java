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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.docker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyException;
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

@Component
public class DockerInspectorManager {
    static final String LATEST_URL = "https://blackducksoftware.github.io/hub-docker-inspector/hub-docker-inspector.sh";
    private final Logger logger = LoggerFactory.getLogger(DockerInspectorManager.class);
    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public ExecutableManager executableManager;

    @Autowired
    public ExecutableRunner executableRunner;

    private boolean hasResolvedInspector;
    private DockerInspectorInfo resolvedInfo;

    public DockerInspectorInfo getDockerInspector(final StrategyEnvironment environment) throws StrategyException {
        try {
            if (!hasResolvedInspector) {
                install();
            }

            return resolvedInfo;
        } catch (final Exception e) {
            throw new StrategyException(e);
        }
    }

    public void install() throws DetectUserFriendlyException, ExecutableRunnerException, IOException {
        hasResolvedInspector = true;

        final DockerInspectorInfo info = resolveShellScript();
        final String bashExecutablePath = executableManager.getExecutablePathOrOverride(ExecutableType.BASH, true, detectConfiguration.getSourceDirectory(), detectConfiguration.getBashPath());
        info.version = resolveInspectorVersion(bashExecutablePath, info.dockerInspectorScript);

        if (info.isOffline) {
            info.offlineDockerInspectorJar = new File(detectConfiguration.getDockerInspectorAirGapPath(), "hub-docker-inspector-" + info.version + ".jar");
            for (final String os : Arrays.asList("ubuntu", "alpine", "centos")) {
                final File osImage = new File(detectConfiguration.getDockerInspectorAirGapPath(), "hub-docker-inspector-" + os + ".tar");
                info.offlineTars.add(osImage);
            }
        }

        resolvedInfo = info;
    }

    private String resolveInspectorVersion(final String bashExecutablePath, final File dockerInspectorShellScript) throws DetectUserFriendlyException {
        try {
            if ("latest".equalsIgnoreCase(detectConfiguration.getDockerInspectorVersion())) {
                final File inspectorDirectory = detectFileManager.getSharedDirectory("docker");
                final List<String> bashArguments = new ArrayList<>();
                bashArguments.add("-c");
                bashArguments.add("\"" + dockerInspectorShellScript.getCanonicalPath() + "\" --version");
                final Executable getDockerInspectorVersion = new Executable(inspectorDirectory, bashExecutablePath, bashArguments);

                final String inspectorVersion = executableRunner.execute(getDockerInspectorVersion).getStandardOutput().split(" ")[1];
                logger.info(String.format("Resolved docker inspector version from latest to: %s", inspectorVersion));
                return inspectorVersion;
            } else {
                return detectConfiguration.getDockerInspectorVersion();
            }
        } catch (final Exception e) {
            throw new DetectUserFriendlyException("Unable to find docker inspector version.", e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }

    private DockerInspectorInfo resolveShellScript() throws DetectUserFriendlyException {
        try {
            final String suppliedDockerVersion = detectConfiguration.getDockerInspectorVersion();
            final File shellScriptFile;
            final File airGapHubDockerInspectorShellScript = new File(detectConfiguration.getDockerInspectorAirGapPath(), "hub-docker-inspector.sh");
            boolean isOffline = false;
            logger.debug(String.format("Verifying air gap shell script present at %s", airGapHubDockerInspectorShellScript.getCanonicalPath()));

            if (StringUtils.isNotBlank(detectConfiguration.getDockerInspectorPath())) {
                shellScriptFile = new File(detectConfiguration.getDockerInspectorPath());
            } else if (airGapHubDockerInspectorShellScript.exists()) {
                shellScriptFile = airGapHubDockerInspectorShellScript;
                isOffline = true;
            } else {
                String hubDockerInspectorShellScriptUrl = LATEST_URL;
                if (!"latest".equals(detectConfiguration.getDockerInspectorVersion())) {
                    hubDockerInspectorShellScriptUrl = String.format("https://blackducksoftware.github.io/hub-docker-inspector/hub-docker-inspector-%s.sh", detectConfiguration.getDockerInspectorVersion());
                }
                logger.info(String.format("Getting the Docker inspector shell script from %s", hubDockerInspectorShellScriptUrl));

                final Request request = new Request.Builder().uri(hubDockerInspectorShellScriptUrl).build();
                String shellScriptContents = null;
                Response response = null;

                try (UnauthenticatedRestConnection restConnection = detectConfiguration.createUnauthenticatedRestConnection(hubDockerInspectorShellScriptUrl)) {
                    response = restConnection.executeRequest(request);
                    shellScriptContents = response.getContentString();
                } finally {
                    ResourceUtil.closeQuietly(response);
                }

                final File inspectorDirectory = detectFileManager.getSharedDirectory("docker");
                shellScriptFile = new File(inspectorDirectory, String.format("hub-docker-inspector-%s.sh", suppliedDockerVersion));
                detectFileManager.writeToFile(shellScriptFile, shellScriptContents);
                shellScriptFile.setExecutable(true);
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
