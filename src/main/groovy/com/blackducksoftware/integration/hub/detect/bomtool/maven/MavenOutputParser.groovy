/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.detect.bomtool.maven

import java.util.regex.Matcher
import java.util.regex.Pattern

import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId

@Component
class MavenOutputParser {
    private final Pattern gavRegex = Pattern.compile("(.*?):(.*?):(.*?):([^:\\n\\r]*)(:(.*))*")

    public List<DependencyNode> parse(final String mavenOutputText) {
        List<DependencyNode> projects = []
        List<DependencyNode> projectStack = []

        List<String> indentaionStrings = ['+- ', '|  ', '\\- ', '   ']

        boolean projectReady = false
        int level = 0
        for (String line : mavenOutputText.split("\n")) {
            if (!line.startsWith("[INFO]")) {
                continue
            }

            line = line.replace("[INFO] ", "")
            if(!line.trim()) {
                continue
            }

            if (line.startsWith('--- ') && line.endsWith(' ---')) {
                projectReady = true
                continue
            }

            if (!projectReady) {
                continue
            }

            if (projectStack.isEmpty()) {
                addToStack(line, projectStack)
                continue
            }

            // the project is ready and the stack is not empty
            int previousLevel = level
            level = 0

            for (String pattern : indentaionStrings) {
                while(line.contains(pattern)) {
                    level++
                    line = line.replaceFirst(Pattern.quote(pattern), '')
                }
            }

            final boolean finished = line.contains('--------')

            if (finished) {
                while (projectStack.size() > 1) {
                    final DependencyNode node = projectStack.pop()
                    projectStack.last().children += node
                }
                projectReady = false
                projects.add(projectStack.pop())
            } else if(textToDependencyNode(line)) {
                if (level == previousLevel) {
                    final DependencyNode currentNode = projectStack.pop()
                    projectStack.last().children += currentNode
                    addToStack(line, projectStack)
                } else if (level > previousLevel) {
                    addToStack(line, projectStack)
                } else {
                    for (; previousLevel >= level; previousLevel--) {
                        final DependencyNode previousNode = projectStack.pop()
                        projectStack.last().children += previousNode
                    }
                    addToStack(line, projectStack)
                }
            }
        }

        return projects
    }

    private void addToStack(final String componentText, final List<DependencyNode> projectStack) {
        final DependencyNode node = textToDependencyNode(componentText)
        if (node) {
            projectStack.push(node)
        }
    }

    private DependencyNode textToDependencyNode(final String componentText) {
        Matcher gavMatcher = gavRegex.matcher(componentText)
        if (!gavMatcher.find()) {
            return null
        }

        String group = gavMatcher.group(1)
        String artifact = gavMatcher.group(2)
        String version = gavMatcher.group(4)

        ExternalId externalId = new MavenExternalId(group, artifact, version)
        def node = new DependencyNode(artifact, version, externalId)
        return node
    }
}
