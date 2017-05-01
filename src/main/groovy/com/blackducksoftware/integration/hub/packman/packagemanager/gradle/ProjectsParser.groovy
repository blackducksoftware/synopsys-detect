package com.blackducksoftware.integration.hub.packman.packagemanager.gradle

import java.util.regex.Matcher
import java.util.regex.Pattern

import org.springframework.stereotype.Component

@Component
class ProjectsParser {
    static final Pattern NO_SUB_PROJECTS_PATTERN = new Pattern('^No sub\\-projects$', Pattern.MULTILINE)

    List<String> extractSubProjectNames(String projects) {
        Matcher checkForNoSubProjects = NO_SUB_PROJECTS_PATTERN.matcher(projects)
        if (checkForNoSubProjects.find()) {
            return Collections.emptyList()
        }

        String previousLine = null
        def projectLineStack = new Stack()
        int treeLevel = 0

        String[] lines = projects.split('\n')
        for (String line : lines) {
        }
    }
}
