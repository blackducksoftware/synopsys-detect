package com.blackducksoftware.integration.hub.packman.packagemanager.gradle

import java.nio.charset.StandardCharsets

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackmanProperties
import com.blackducksoftware.integration.hub.packman.util.executable.Executable
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableRunner
import com.google.gson.Gson

@Component
class GradleInitScriptPackager {
    private final Logger logger = LoggerFactory.getLogger(GradleInitScriptPackager.class)

    @Autowired
    Gson gson

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    PackmanProperties packmanProperties

    DependencyNode extractRootProjectNode(String sourcePath, String gradleExecutable) {
        File initScriptFile = File.createTempFile('init-packman', '.gradle')
        initScriptFile.deleteOnExit()
        String initScriptContents = getClass().getResourceAsStream('/init-script-gradle').getText(StandardCharsets.UTF_8.name())
        initScriptContents = initScriptContents.replace('EXCLUDED_PROJECT_NAMES', packmanProperties.excludedProjectNames)
        initScriptContents = initScriptContents.replace('INCLUDED_PROJECT_NAMES', packmanProperties.includedProjectNames)
        initScriptContents = initScriptContents.replace('EXCLUDED_CONFIGURATION_NAMES', packmanProperties.excludedConfigurationNames)
        initScriptContents = initScriptContents.replace('INCLUDED_CONFIGURATION_NAMES', packmanProperties.includedConfigurationNames)

        initScriptFile << initScriptContents
        String initScriptPath = initScriptFile.absolutePath
        logger.info("using ${initScriptPath} as the path for the gradle init script")
        Executable executable = new Executable(new File(sourcePath), gradleExecutable, [
            packmanProperties.gradleBuildCommand,
            "--init-script=${initScriptPath}"
        ])
        executableRunner.executeLoudly(executable)

        File buildDirectory = new File(sourcePath, 'build')
        File blackduckDirectory = new File(buildDirectory, 'blackduck')
        File dependencyNodeFile = new File(blackduckDirectory, 'dependencyNodes.json')
        String dependencyNodeJson = dependencyNodeFile.getText(StandardCharsets.UTF_8.name())
        DependencyNode rootProjectDependencyNode = gson.fromJson(dependencyNodeJson, DependencyNode.class)

        blackduckDirectory.deleteDir()

        rootProjectDependencyNode
    }
}
