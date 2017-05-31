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
import com.blackducksoftware.integration.hub.packman.type.ExecutableType
import com.blackducksoftware.integration.hub.packman.type.PackageManagerType
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableManager

@Component
class GradlePackageManager extends PackageManager {
    private final Logger logger = LoggerFactory.getLogger(GradlePackageManager.class)

    static final String BUILD_GRADLE = 'build.gradle'

    @Autowired
    GradleInitScriptPackager gradleInitScriptPackager

    @Autowired
    ExecutableManager executableManager

    @Autowired
    FileFinder fileFinder

    @ValueDescription(description="Path of the Gradle executable")
    @Value('${packman.gradle.path}')
    String gradlePath

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.GRADLE
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        def gradleExecutable = findGradleExecutable(sourcePath)
        def buildGradle = fileFinder.findFile(sourcePath, BUILD_GRADLE)

        gradleExecutable && buildGradle
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        def gradleExecutable = findGradleExecutable(sourcePath)
        DependencyNode rootProjectNode = gradleInitScriptPackager.extractRootProjectNode(sourcePath, gradleExecutable)
        [rootProjectNode]
    }

    private String findGradleExecutable(String sourcePath) {
        if (StringUtils.isBlank(gradlePath)) {
            logger.info('packman.gradle.path not set in config - first try to find the gradle wrapper')
            gradlePath = executableManager.getPathOfExecutable(sourcePath, ExecutableType.GRADLEW)
        }

        if (StringUtils.isBlank(gradlePath)) {
            logger.info('gradle wrapper not found - trying to find gradle on the PATH')
            gradlePath = executableManager.getPathOfExecutable(ExecutableType.GRADLE)
        }

        gradlePath
    }
}