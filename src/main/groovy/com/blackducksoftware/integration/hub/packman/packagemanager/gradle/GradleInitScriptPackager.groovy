package com.blackducksoftware.integration.hub.packman.packagemanager.gradle

import java.nio.charset.StandardCharsets

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.util.command.Executable
import com.blackducksoftware.integration.hub.packman.util.command.ExecutableManager
import com.blackducksoftware.integration.hub.packman.util.command.ExecutableRunner
import com.google.gson.Gson

@Component
class GradleInitScriptPackager {
    private final Logger logger = LoggerFactory.getLogger(GradleInitScriptPackager.class)

    @Value('${packman.gradle.build.command}')
    String gradleBuildCommand

    @Value('${packman.gradle.excluded.configurations}')
    String excludedConfigurationNames

    @Value('${packman.gradle.included.configurations}')
    String includedConfigurationNames

    @Value('${packman.gradle.excluded.projects}')
    String excludedProjectNames

    @Value('${packman.gradle.included.projects}')
    String includedProjectNames

    @Autowired
    Gson gson

    @Autowired
    ExecutableManager commandManager

    @Autowired
    ExecutableRunner commandRunner

    DependencyNode extractRootProjectNode(String gradleCommand, String sourcePath) {
        File initScriptFile = File.createTempFile('init-packman', '.gradle')
        initScriptFile.deleteOnExit()
        String initScriptContents = getClass().getResourceAsStream('/init-script-gradle').getText(StandardCharsets.UTF_8.name())
        initScriptContents = initScriptContents.replace('EXCLUDED_PROJECT_NAMES', excludedProjectNames)
        initScriptContents = initScriptContents.replace('INCLUDED_PROJECT_NAMES', includedProjectNames)
        initScriptContents = initScriptContents.replace('EXCLUDED_CONFIGURATION_NAMES', excludedConfigurationNames)
        initScriptContents = initScriptContents.replace('INCLUDED_CONFIGURATION_NAMES', includedConfigurationNames)

        initScriptFile << initScriptContents
        String initScriptPath = initScriptFile.absolutePath
        logger.info("using ${initScriptPath} as the path for the gradle init script")
        Executable command = new Executable(new File(sourcePath), gradleCommand, [
            gradleBuildCommand,
            "--init-script=${initScriptPath}"
        ])
        commandRunner.executeLoudly(command)

        File buildDirectory = new File(sourcePath, 'build')
        File blackduckDirectory = new File(buildDirectory, 'blackduck')
        File dependencyNodeFile = new File(blackduckDirectory, 'dependencyNodes.json')
        String dependencyNodeJson = dependencyNodeFile.getText(StandardCharsets.UTF_8.name())
        DependencyNode rootProjectDependencyNode = gson.fromJson(dependencyNodeJson, DependencyNode.class)

        blackduckDirectory.deleteDir()

        rootProjectDependencyNode
    }
}
