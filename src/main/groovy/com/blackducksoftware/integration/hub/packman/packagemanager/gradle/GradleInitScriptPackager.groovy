package com.blackducksoftware.integration.hub.packman.packagemanager.gradle

import java.nio.charset.StandardCharsets

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.type.CommandType
import com.blackducksoftware.integration.hub.packman.util.command.CommandManager
import com.google.gson.Gson

@Component
class GradleInitScriptPackager {
    private final Logger logger = LoggerFactory.getLogger(GradleInitScriptPackager.class)

    @Value('${packman.gradle.path}')
    String gradlePath

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
    CommandManager commandManager

    DependencyNode extractRootProjectNode(String sourcePath) {
        if (!gradlePath) {
            logger.info('packman.gradle.path not set in config - first try to find the gradle wrapper')
            gradlePath = commandManager.getPathOfCommand(sourcePath, CommandType.GRADLEW)
        }

        if (!gradlePath) {
            logger.info('gradle wrapper not found - trying to find gradle on the PATH')
            gradlePath = commandManager.getPathOfCommand(CommandType.GRADLE)
        }

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
        String output = "${gradlePath} ${gradleBuildCommand} --init-script=${initScriptPath}".execute(null, new File(sourcePath)).text
        logger.debug(output)

        File buildDirectory = new File(sourcePath, 'build')
        File blackduckDirectory = new File(buildDirectory, 'blackduck')
        File dependencyNodeFile = new File(blackduckDirectory, 'dependencyNodes.json')
        String dependencyNodeJson = dependencyNodeFile.getText(StandardCharsets.UTF_8.name())
        DependencyNode rootProjectDependencyNode = gson.fromJson(dependencyNodeJson, DependencyNode.class)

        blackduckDirectory.deleteDir()

        rootProjectDependencyNode
    }
}
