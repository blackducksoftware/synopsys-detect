/**
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.detect.bomtool.maven;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId;

@Component
public class MavenOutputParser {
    private final Pattern beginProjectRegex = Pattern.compile("--- .*? ---");

    private final Pattern gavRegex = Pattern.compile("(.*?):(.*?):(.*?):([^:\\n\\r]*)(:(.*))*");

    private final Pattern plusRegex = Pattern.compile("\\+- ");

    private final Pattern pipeRegex = Pattern.compile("\\|  ");

    private final Pattern endRegex = Pattern.compile("\\\\- ");

    private final Pattern emptyRegex = Pattern.compile("   ");

    private final Pattern finishRegex = Pattern.compile("--------");

    public List<DependencyNode> parse(final String mavenOutputText) {
        final List<DependencyNode> projects = new ArrayList<>();

        final Stack<DependencyNode> projectStack = new Stack<>();

        boolean projectReady = false;
        int level = 0;
        for (String line : mavenOutputText.split("\n")) {
            if (!line.startsWith("[INFO]")) {
                continue;
            }

            line = line.replace("[INFO] ", "");
            if (StringUtils.isBlank(line)) {
                continue;
            }

            final Matcher beginProjectMatcher = beginProjectRegex.matcher(line);
            if (beginProjectMatcher.matches()) {
                projectReady = true;
                continue;
            }

            if (!projectReady) {
                continue;
            }

            if (projectStack.isEmpty()) {
                addToStack(line, projectStack);
                continue;
            }

            // the project is ready and the stack is not empty
            int previousLevel = level;
            level = 0;

            final Matcher plusMatcher = plusRegex.matcher(line);
            if (plusMatcher.find()) {
                line = line.replace(plusMatcher.group(), "");
                level++;
            }

            final Matcher pipeMatcher = pipeRegex.matcher(line);
            while (pipeMatcher.find()) {
                line = line.replace(pipeMatcher.group(), "");
                level++;
            }

            final Matcher endMatcher = endRegex.matcher(line);
            if (endMatcher.find()) {
                line = line.replace(endMatcher.group(), "");
                level++;
            }

            final Matcher emptyMatcher = emptyRegex.matcher(line);
            while (emptyMatcher.find()) {
                line = line.replace(emptyMatcher.group(), "");
                level++;
            }

            final Matcher finishMatcher = finishRegex.matcher(line);
            final boolean finished = finishMatcher.find();

            if (lineIsValid(line)) {
                if (finished) {

                } else if (level == previousLevel) {
                    final DependencyNode currentNode = projectStack.pop();
                    projectStack.peek().children.add(currentNode);
                    addToStack(line, projectStack);
                } else if (level > previousLevel) {
                    addToStack(line, projectStack);
                } else if (level < previousLevel) {
                    for (; previousLevel >= level; previousLevel--) {
                        final DependencyNode previousNode = projectStack.pop();
                        projectStack.peek().children.add(previousNode);
                    }
                    addToStack(line, projectStack);
                }
            }

            if (finished) {
                while (projectStack.size() > 1) {
                    final DependencyNode node = projectStack.pop();
                    projectStack.peek().children.add(node);
                }
                projectReady = false;
                projects.add(projectStack.pop());
            }
        }

        return projects;
    }

    private boolean lineIsValid(final String line) {
        return textToDependencyNode(line) != null;
    }

    private boolean addToStack(final String componentText, final Stack<DependencyNode> projectStack) {
        final DependencyNode node = textToDependencyNode(componentText);
        boolean valid = false;
        if (node != null) {
            valid = projectStack.push(node) != null;
        }
        return valid;
    }

    private DependencyNode textToDependencyNode(final String componentText) {
        final Matcher gavMatcher = gavRegex.matcher(componentText);
        if (!gavMatcher.find()) {
            return null;
        }

        final String group = gavMatcher.group(1);
        final String artifact = gavMatcher.group(2);
        final String version = gavMatcher.group(4);

        final ExternalId externalId = new MavenExternalId(group, artifact, version);
        final DependencyNode node = new DependencyNode(artifact, version, externalId);
        return node;
    }

}
