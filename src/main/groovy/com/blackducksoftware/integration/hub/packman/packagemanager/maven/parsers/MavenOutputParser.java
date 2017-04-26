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
package com.blackducksoftware.integration.hub.packman.packagemanager.maven.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId;

public class MavenOutputParser {
    private final Logger logger = LoggerFactory.getLogger(MavenOutputParser.class);

    private final Pattern beginProjectRegex = Pattern.compile("--- .*? ---");

    private final Pattern gavRegex = Pattern.compile("(.*?):(.*?):(.*?):([^:\\n\\r]*).*");

    private final Pattern plusRegex = Pattern.compile("\\+- ");

    private final Pattern pipeRegex = Pattern.compile("\\|  ");

    private final Pattern endRegex = Pattern.compile("\\\\- ");

    private final Pattern emptyRegex = Pattern.compile("   ");

    private final Pattern finishRegex = Pattern.compile("--------");

    public List<DependencyNode> parse(final BufferedReader bufferedReader) throws IOException {
        final List<DependencyNode> projects = new ArrayList<>();

        final Stack<DependencyNode> projectStack = new Stack<>();

        boolean projectReady = false;
        String line;
        int level = 0;
        while ((line = bufferedReader.readLine()) != null) {

            line = line.replace("[INFO] ", "");

            final Matcher beginProjectMatcher = beginProjectRegex.matcher(line);

            if (StringUtils.isBlank(line)) {

            } else if (beginProjectMatcher.matches()) {
                projectReady = true;
            } else if (projectReady && projectStack.isEmpty()) {
                addToStack(line, projectStack);
            } else if (projectReady && !projectStack.isEmpty()) {
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

                if (finished) {

                } else if (level == previousLevel && lineIsValid(line)) {
                    final DependencyNode currentNode = projectStack.pop();
                    projectStack.peek().children.add(currentNode);
                    addToStack(line, projectStack);
                } else if (level > previousLevel) {
                    if (lineIsValid(line)) {
                        addToStack(line, projectStack);
                    } else {
                        level = previousLevel;
                    }
                } else if (level < previousLevel) {
                    if (lineIsValid(line)) {
                        for (; previousLevel >= level; previousLevel--) {
                            final DependencyNode previousNode = projectStack.pop();
                            projectStack.peek().children.add(previousNode);
                        }
                        addToStack(line, projectStack);
                    } else {
                        level = previousLevel;
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
        }

        return projects;
    }

    private boolean lineIsValid(final String line) {
        return componentToDependencyNode(line) != null;
    }

    private boolean addToStack(final String component, final Stack<DependencyNode> projectStack) {
        final DependencyNode node = componentToDependencyNode(component);
        boolean valid = false;
        if (node != null) {
            valid = projectStack.push(node) != null;
        }
        return valid;
    }

    private DependencyNode componentToDependencyNode(final String component) {
        final Matcher gavMatcher = gavRegex.matcher(component);
        if (gavMatcher.find()) {
            final String group = gavMatcher.group(1);
            final String artifact = gavMatcher.group(2);
            final String version = gavMatcher.group(4);
            final ExternalId externalId = new MavenExternalId(Forge.maven, group, artifact, version);
            final DependencyNode node = new DependencyNode(artifact, version, externalId);
            return node;
        }
        return null;
    }
}
