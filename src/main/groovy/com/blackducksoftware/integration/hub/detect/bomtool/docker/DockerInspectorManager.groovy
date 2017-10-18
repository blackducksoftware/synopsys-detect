package com.blackducksoftware.integration.hub.detect.bomtool.docker

import org.apache.commons.lang3.math.NumberUtils
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
                UnauthenticatedRestConnection restConnection = new UnauthenticatedRestConnection(new Slf4jIntLogger(logger), hubDockerInspectorShellScriptUrl, detectConfiguration.getHubTimeout())
                restConnection.alwaysTrustServerCertificate = detectConfiguration.hubTrustCertificate
                restConnection.proxyHost = detectConfiguration.getHubProxyHost()
                restConnection.proxyPort = NumberUtils.toInt(detectConfiguration.getHubProxyPort())
                restConnection.proxyUsername = detectConfiguration.getHubProxyUsername()
                restConnection.proxyPassword = detectConfiguration.getHubProxyPassword()
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
