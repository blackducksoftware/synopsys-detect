/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.bomtool.docker

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleInspectorManager
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner
import com.blackducksoftware.integration.hub.proxy.ProxyInfo
import com.blackducksoftware.integration.hub.proxy.ProxyInfoBuilder
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnection
import com.blackducksoftware.integration.log.Slf4jIntLogger

import groovy.transform.TypeChecked
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response

@Component
@TypeChecked
class DockerInspectorManager {
    private final Logger logger = LoggerFactory.getLogger(GradleInspectorManager.class)

    static final URL LATEST_URL = new URL('https://blackducksoftware.github.io/hub-docker-inspector/hub-docker-inspector.sh')

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    DetectFileManager detectFileManager

    @Autowired
    ExecutableRunner executableRunner

    private File dockerInspectorShellScript
    private String inspectorVersion

    String getInspectorVersion(String bashExecutablePath) {
        if ('latest'.equalsIgnoreCase(detectConfiguration.getDockerInspectorVersion())) {
            if (!inspectorVersion) {
                File dockerPropertiesFile = detectFileManager.createFile(BomToolType.DOCKER, 'application.properties')
                File dockerBomToolDirectory =  dockerPropertiesFile.getParentFile()
                if(!dockerInspectorShellScript) {
                    dockerInspectorShellScript = getShellScript()
                }
                List<String> bashArguments = [
                    '-c',
                    "\"${dockerInspectorShellScript.getCanonicalPath()}\" --version" as String
                ]
                Executable getDockerInspectorVersion = new Executable(dockerBomToolDirectory, bashExecutablePath, bashArguments)

                inspectorVersion = executableRunner.execute(getDockerInspectorVersion).standardOutput.split(' ')[1]
            }
        } else {
            inspectorVersion = detectConfiguration.getDockerInspectorVersion()
        }
        inspectorVersion
    }

    private File getShellScript() {
        if (!dockerInspectorShellScript) {
            File shellScriptFile
            def airGapHubDockerInspectorShellScript = new File(detectConfiguration.getDockerInspectorAirGapPath(), 'hub-docker-inspector.sh')
            logger.debug("Verifying air gap shell script present at ${airGapHubDockerInspectorShellScript.getCanonicalPath()}")

            if (detectConfiguration.dockerInspectorPath) {
                shellScriptFile = new File(detectConfiguration.dockerInspectorPath)
            } else if (airGapHubDockerInspectorShellScript.exists()) {
                shellScriptFile = airGapHubDockerInspectorShellScript
            } else {
                URL hubDockerInspectorShellScriptUrl = LATEST_URL
                if (!'latest'.equals(detectConfiguration.dockerInspectorVersion)) {
                    hubDockerInspectorShellScriptUrl = new URL("https://blackducksoftware.github.io/hub-docker-inspector/hub-docker-inspector-${detectConfiguration.dockerInspectorVersion}.sh")
                }
                logger.info("Getting the Docker inspector shell script from ${hubDockerInspectorShellScriptUrl.toURI().toString()}")
                ProxyInfoBuilder proxyInfoBuilder = new ProxyInfoBuilder()
                proxyInfoBuilder.setHost(detectConfiguration.getHubProxyHost())
                proxyInfoBuilder.setPort(detectConfiguration.getHubProxyPort())
                proxyInfoBuilder.setUsername(detectConfiguration.getHubProxyUsername())
                proxyInfoBuilder.setPassword(detectConfiguration.getHubProxyPassword())
                ProxyInfo proxyInfo = proxyInfoBuilder.build()
                UnauthenticatedRestConnection restConnection = new UnauthenticatedRestConnection(new Slf4jIntLogger(logger), hubDockerInspectorShellScriptUrl, detectConfiguration.getHubTimeout(), proxyInfo)
                restConnection.alwaysTrustServerCertificate = detectConfiguration.hubTrustCertificate
                HttpUrl httpUrl = restConnection.createHttpUrl()
                Request request = restConnection.createGetRequest(httpUrl)
                String shellScriptContents = null
                Response response = null
                try {
                    response = restConnection.handleExecuteClientCall(request)
                    shellScriptContents =  response.body().string()
                } finally {
                    if (response != null) {
                        response.close()
                    }
                }
                shellScriptFile = detectFileManager.createFile(BomToolType.DOCKER, "hub-docker-inspector-${detectConfiguration.dockerInspectorVersion}.sh")
                detectFileManager.writeToFile(shellScriptFile, shellScriptContents)
                shellScriptFile.setExecutable(true)
            }
            dockerInspectorShellScript = shellScriptFile
        }
        return dockerInspectorShellScript
    }
}
