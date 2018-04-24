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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolInspectorManager;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.request.Request;
import com.blackducksoftware.integration.hub.request.Response;
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnection;

@Component
public class DockerInspectorManager extends BomToolInspectorManager {
    static final String LATEST_URL = "https://blackducksoftware.github.io/hub-docker-inspector/hub-docker-inspector.sh";
    private final Logger logger = LoggerFactory.getLogger(DockerInspectorManager.class);

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private DetectFileManager detectFileManager;

    @Autowired
    private ExecutableRunner executableRunner;

    private File dockerInspectorShellScript;
    private String inspectorVersion;

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.DOCKER;
    }

    @Override
    public void install() throws DetectUserFriendlyException {
        //Unlike the other inspectors, this inspector must install and then ask for version.
        dockerInspectorShellScript = resolveShellScript();
        final String bashExecutablePath = executableManager.getExecutablePathOrOverride(ExecutableType.BASH, true, detectConfiguration.getSourceDirectory(), detectConfiguration.getBashPath());
        inspectorVersion = resolveInspectorVersion(bashExecutablePath, dockerInspectorShellScript);
    }

    public String getInspectorVersion() {
        return inspectorVersion;
    }

    public File getShellScript() {
        return dockerInspectorShellScript;
    }

    private String resolveInspectorVersion(final String bashExecutablePath, final File dockerInspectorShellScript) throws DetectUserFriendlyException {
        try {
            if ("latest".equalsIgnoreCase(detectConfiguration.getDockerInspectorVersion())) {
                final File dockerPropertiesFile = detectFileManager.createFile(BomToolType.DOCKER, "application.properties");
                final File dockerBomToolDirectory = dockerPropertiesFile.getParentFile();
                final List<String> bashArguments = new ArrayList<>();
                bashArguments.add("-c");
                bashArguments.add("\"" + dockerInspectorShellScript.getCanonicalPath() + "\" --version");
                final Executable getDockerInspectorVersion = new Executable(dockerBomToolDirectory, bashExecutablePath, bashArguments);

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

    private File resolveShellScript() throws DetectUserFriendlyException {
        try {
            final String suppliedDockerVersion = detectConfiguration.getDockerInspectorVersion();
            final File shellScriptFile;
            final File airGapHubDockerInspectorShellScript = new File(detectConfiguration.getDockerInspectorAirGapPath(), "hub-docker-inspector.sh");
            logger.debug(String.format("Verifying air gap shell script present at %s", airGapHubDockerInspectorShellScript.getCanonicalPath()));

            if (StringUtils.isNotBlank(detectConfiguration.getDockerInspectorPath())) {
                shellScriptFile = new File(detectConfiguration.getDockerInspectorPath());
            } else if (airGapHubDockerInspectorShellScript.exists()) {
                shellScriptFile = airGapHubDockerInspectorShellScript;
            } else {
                String hubDockerInspectorShellScriptUrl = LATEST_URL;
                if (!"latest".equals(detectConfiguration.getDockerInspectorVersion())) {
                    hubDockerInspectorShellScriptUrl = String.format("https://blackducksoftware.github.io/hub-docker-inspector/hub-docker-inspector-%s.sh", detectConfiguration.getDockerInspectorVersion());
                }
                logger.info(String.format("Getting the Docker inspector shell script from %s", hubDockerInspectorShellScriptUrl));
                final UnauthenticatedRestConnection restConnection = detectConfiguration.createUnauthenticatedRestConnection(hubDockerInspectorShellScriptUrl);

                final Request request = new Request.Builder().uri(hubDockerInspectorShellScriptUrl).build();
                String shellScriptContents = null;
                Response response = null;
                try {
                    response = restConnection.executeRequest(request);
                    shellScriptContents = response.getContentString();
                } finally {
                    if (response != null) {
                        response.close();
                    }
                }
                shellScriptFile = detectFileManager.createFile(BomToolType.DOCKER, String.format("hub-docker-inspector-%s.sh", suppliedDockerVersion));
                detectFileManager.writeToFile(shellScriptFile, shellScriptContents);
                shellScriptFile.setExecutable(true);
            }
            return shellScriptFile;
        } catch (final Exception e) {
            throw new DetectUserFriendlyException(String.format("There was a problem retrieving the docker inspector shell script: %s", e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }

}
