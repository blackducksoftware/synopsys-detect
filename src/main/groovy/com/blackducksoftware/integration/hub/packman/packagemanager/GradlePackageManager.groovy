package com.blackducksoftware.integration.hub.packman.packagemanager

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.help.ValueDescription
import com.blackducksoftware.integration.hub.packman.packagemanager.gradle.GradleInitScriptPackager
import com.blackducksoftware.integration.hub.packman.type.CommandType
import com.blackducksoftware.integration.hub.packman.type.PackageManagerType
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.command.CommandManager

@Component
class GradlePackageManager extends PackageManager {
    private final Logger logger = LoggerFactory.getLogger(GradlePackageManager.class)

    static final String BUILD_GRADLE = 'build.gradle'

    @Autowired
    GradleInitScriptPackager gradleInitScriptPackager

    @Autowired
    CommandManager commandManager

    @Autowired
    FileFinder fileFinder

    @ValueDescription(description="Path of the Gradle executable")
    @Value('${packman.gradle.path}')
    String gradlePath

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.GRADLE
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        def gradleCommand = findGradleCommand(sourcePath)
        def buildGradle = fileFinder.findFile(sourcePath, BUILD_GRADLE)

        gradleCommand && buildGradle
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        def gradleCommand = findGradleCommand(sourcePath)
        DependencyNode rootProjectNode = gradleInitScriptPackager.extractRootProjectNode(gradleCommand, sourcePath)
        [rootProjectNode]
    }

    private String findGradleCommand(String sourcePath) {
        if (StringUtils.isBlank(gradlePath)) {
            logger.info('packman.gradle.path not set in config - first try to find the gradle wrapper')
            gradlePath = commandManager.getPathOfCommand(sourcePath, CommandType.GRADLEW)
        }

        if (StringUtils.isBlank(gradlePath)) {
            logger.info('gradle wrapper not found - trying to find gradle on the PATH')
            gradlePath = commandManager.getPathOfCommand(CommandType.GRADLE)
        }

        gradlePath
    }
}