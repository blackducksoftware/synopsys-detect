package com.blackducksoftware.integration.hub.packman.packagemanager.gradle

import java.util.regex.Matcher
import java.util.regex.Pattern

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.util.ExcludedIncludedFilter

@Component
class ProjectsParser {
    private final Logger logger = LoggerFactory.getLogger(ProjectsParser.class)

    static final String PROJECT_INDICATOR = '--- Project \''
    static final String FIRST_PROJECT_INDICATOR = "+${PROJECT_INDICATOR}"
    static final Pattern NO_SUB_PROJECTS_PATTERN = new Pattern('^No sub\\-projects$', Pattern.MULTILINE)

    void populateWithSubProjects(GradleProjectName gradleProjectName, String projects, ExcludedIncludedFilter projectNamesFilter) {
        Matcher checkForNoSubProjects = NO_SUB_PROJECTS_PATTERN.matcher(projects)
        if (checkForNoSubProjects.find()) {
            return
        }

        boolean processingProjects = false
        String previousLine = null
        GradleProjectName previousName
        def projectNameStack = new Stack()
        projectNameStack.push(gradleProjectName)
        int treeLevel = 0

        String[] lines = projects.split('\n')
        for (String line : lines) {
            if (!processingProjects && line.contains(FIRST_PROJECT_INDICATOR)) {
                processingProjects = true
            }

            if (!processingProjects) {
                continue
            }

            if (line.contains(PROJECT_INDICATOR)) {
                GradleProjectName projectName = parseProjectNameFromOutputLine(line)

                int lineTreeLevel = StringUtils.countMatches(line, '    ')
                if (lineTreeLevel == treeLevel + 1) {
                    projectNameStack.push(previousName)
                } else if (lineTreeLevel < treeLevel) {
                    (treeLevel - lineTreeLevel).times { projectNameStack.pop() }
                } else if (lineTreeLevel != treeLevel) {
                    logger.error "The tree level (${treeLevel}) and this line (${line}) with count ${lineTreeLevel} can't be reconciled."
                }

                projectNameStack.peek().children.add(projectName)
                previousName = projectName
                treeLevel = lineTreeLevel
            }

            previousLine = line
        }

        filterProjectNames(gradleProjectName, projectNamesFilter)
    }

    GradleProjectName parseProjectNameFromOutputLine(String outputLine) {
        int startIndex = outputLine.indexOf(PROJECT_INDICATOR) + PROJECT_INDICATOR.length()
        int endIndex = outputLine.indexOf('\' - ', startIndex)

        def gradleProjectName = new GradleProjectName()
        gradleProjectName.name = outputLine[startIndex..(endIndex - 1)]
        gradleProjectName
    }

    void filterProjectNames(GradleProjectName gradleProjectName, ExcludedIncludedFilter projectNamesFilter) {
        List<GradleProjectName> filteredChildren = []
        gradleProjectName.children.each() {
            String childName = it.name
            //gradle prepends a colon to all subproject names
            if (childName[0] == ':') {
                childName = childName[1..-1]
            }
            if (projectNamesFilter.shouldInclude(childName)) {
                filteredChildren.add(it)
                filterProjectNames(it, projectNamesFilter)
            }
        }
        gradleProjectName.children = filteredChildren
    }
}