package com.blackducksoftware.integration.hub.detect.bomtool.gradle

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeBuilder
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation

import groovy.transform.TypeChecked

@TypeChecked
@Component
class GradleDependenciesParser {
    private final Logger logger = LoggerFactory.getLogger(GradleDependenciesParser.class)

    static final String FIRST_COMPONENT_OF_CONFIGURATION = '+---'
    static final String COMPONENT_PREFIX = '--- '
    static final String SEEN_ELSEWHERE_SUFFIX = ' (*)'
    static final String WINNING_VERSION_INDICATOR = ' -> '

    DetectCodeLocation parseDependencies(InputStream dependenciesInputStream) {

        //TODO get root project information
        String rootProjectSourcePath = ""
        String rootProjectGroup  = ""
        String rootProjectName  = ""
        String rootProjectVersionName  = ""

        //TODO get project information
        String projectSourcePath = ""
        String projectGroup = ""
        String projectName = ""
        String projectVersionName = ""

        DependencyNode tempRoot = new DependencyNode("project", "version", new MavenExternalId("group", "project", "version"))

        DependencyNodeBuilder dependencyNodeBuilder = new DependencyNodeBuilder(tempRoot)
        boolean processingConfiguration = false
        String configurationName = null
        String previousLine = null
        Stack<DependencyNode> nodeStack = new Stack<>()
        nodeStack.push(tempRoot)
        DependencyNode previousNode = null
        int treeLevel = 0

        dependenciesInputStream.eachLine("UTF-8"){ line ->
            if (StringUtils.isBlank(line)) {
                processingConfiguration = false
                configurationName = null
                previousLine = null
                nodeStack = new Stack()
                nodeStack.push(tempRoot)
                previousNode = null
                treeLevel = 0
                return
            }
            if (!processingConfiguration && line.startsWith(FIRST_COMPONENT_OF_CONFIGURATION)) {
                processingConfiguration = true
                configurationName = previousLine.substring(0, previousLine.indexOf(' - ')).trim()
                logger.info("processing of configuration ${configurationName} started")
            }
            if (!processingConfiguration) {
                previousLine = line
                return
            }

            DependencyNode lineNode = createDependencyNodeFromOutputLine(line)
            if (lineNode == null) {
                previousLine = line
                return
            }

            int lineTreeLevel = StringUtils.countMatches(line, '    ')
            if (lineTreeLevel == treeLevel + 1) {
                nodeStack.push(previousNode)
            } else if (lineTreeLevel < treeLevel) {
                (treeLevel - lineTreeLevel).times { nodeStack.pop() }
            } else if (lineTreeLevel != treeLevel) {
                logger.error "The tree level (${treeLevel}) and this line (${line}) with count ${lineTreeLevel} can't be reconciled."
            }
            dependencyNodeBuilder.addChildNodeWithParents(lineNode, [nodeStack.peek()])
            previousNode = lineNode
            treeLevel = lineTreeLevel
            previousLine = line
        }

        //TODO create the correct DetectCodeLocation, is there/should there be a difference between a single standalone Gradle Project vs. a multi Gradle project structure?
        new DetectCodeLocation(BomToolType.GRADLE, projectSourcePath, rootProjectName, rootProjectVersionName,
                new MavenExternalId(rootProjectGroup, rootProjectName, rootProjectVersionName), tempRoot.children)
    }

    DependencyNode createDependencyNodeFromOutputLine(String outputLine) {
        if (StringUtils.isBlank(outputLine) || !outputLine.contains(COMPONENT_PREFIX)) {
            logger.warn 'No input was provided.'
            return null
        }

        String cleanedOutput = StringUtils.trimToEmpty(outputLine)
        if (cleanedOutput.split(':').length != 3) {
            logger.error "The line can not be reasonably split in to the neccessary parts: ${outputLine}"
            return null
        }

        cleanedOutput = cleanedOutput.substring(cleanedOutput.indexOf(COMPONENT_PREFIX) + COMPONENT_PREFIX.length())
        if (cleanedOutput.endsWith(SEEN_ELSEWHERE_SUFFIX)) {
            cleanedOutput = cleanedOutput[0..(-1 * (SEEN_ELSEWHERE_SUFFIX.length() + 1))]
        }

        String[] gav = cleanedOutput.split(':')
        String group = gav[0]
        String artifact = gav[1]
        String version = gav[2]
        if (version.contains(WINNING_VERSION_INDICATOR)) {
            int winningVersionIndex = version.indexOf(WINNING_VERSION_INDICATOR) + WINNING_VERSION_INDICATOR.length()
            version = version[winningVersionIndex..-1]
        }

        new DependencyNode(artifact, version, new MavenExternalId(group, artifact, version))
    }
}