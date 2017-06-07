package com.blackducksoftware.integration.hub.packman.bomtool

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.bomtool.gradle.GradleInitScriptPackager
import com.blackducksoftware.integration.hub.packman.type.BomToolType
import com.blackducksoftware.integration.hub.packman.type.ExecutableType

@Component
class GradleBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(GradleBomTool.class)

    static final String BUILD_GRADLE = 'build.gradle'

    @Autowired
    GradleInitScriptPackager gradleInitScriptPackager

    Map<String, String> matchingSourcePathToGradleExecutable = [:]

    BomToolType getBomToolType() {
        return BomToolType.GRADLE
    }

    boolean isBomToolApplicable() {
        packmanProperties.getSourcePaths().each { sourcePath ->
            def gradleExecutable = findGradleExecutable(sourcePath)
            def buildGradle = fileFinder.findFile(sourcePath, BUILD_GRADLE)
            if (gradleExecutable && buildGradle) {
                matchingSourcePathToGradleExecutable.put(sourcePath, gradleExecutable)
            }
        }

        !matchingSourcePathToGradleExecutable.empty
    }

    List<DependencyNode> extractDependencyNodes() {
        List<DependencyNode> projectNodes = []
        matchingSourcePathToGradleExecutable.each { sourcePath, gradleExecutable ->
            DependencyNode rootProjectNode = gradleInitScriptPackager.extractRootProjectNode(sourcePath, gradleExecutable)
            projectNodes.add(rootProjectNode)
        }

        projectNodes
    }

    private String findGradleExecutable(String sourcePath) {
        String gradlePath = packmanProperties.getGradlePath()
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