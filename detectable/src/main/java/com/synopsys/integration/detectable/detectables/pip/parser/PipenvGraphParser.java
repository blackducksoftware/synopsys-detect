/**
 * hub-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.synopsys.integration.detectable.detectables.pip.parser;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocationType;
import com.synopsys.integration.detectable.detectables.pip.model.PipParseResult;

public class PipenvGraphParser {
    public static final String TOP_LEVEL_SEPARATOR = "==";
    public static final String DEPENDENCY_INDENTATION = "  ";
    public static final String DEPENDENCY_NAME_PREFIX = "- ";
    public static final String DEPENDENCY_NAME_SUFFIX = " [";
    public static final String DEPENDENCY_VERSION_PREFIX = "installed: ";
    public static final String DEPENDENCY_VERSION_SUFFIX = "]";

    private final ExternalIdFactory externalIdFactory;

    public PipenvGraphParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public PipParseResult parse(final String projectName, final String projectVersionName, final List<String> pipFreezeOutput, final List<String> pipenvGraphOutput, final String sourcePath) {
        final MutableMapDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        final Stack<Dependency> dependencyStack = new Stack<>();

        final Map<String, String[]> pipFreezeMap = pipFreezeOutput.stream()
                                                       .map(line -> line.split(TOP_LEVEL_SEPARATOR))
                                                       .filter(splitLine -> splitLine.length == 2)
                                                       .collect(Collectors.toMap(splitLine -> splitLine[0].trim().toLowerCase(), splitLine -> splitLine));

        int lastLevel = -1;
        for (final String line : pipenvGraphOutput) {
            final int currentLevel = getLevel(line);
            final Optional<Dependency> parsedDependency = getDependencyFromLine(pipFreezeMap, line);

            if (!parsedDependency.isPresent()) {
                continue;
            }

            final Dependency dependency = parsedDependency.get();

            if (currentLevel == lastLevel) {
                dependencyStack.pop();
            } else {
                for (; lastLevel >= currentLevel; lastLevel--) {
                    dependencyStack.pop();
                }
            }

            if (dependencyStack.size() > 0) {
                dependencyGraph.addChildWithParent(dependency, dependencyStack.peek());
            } else {
                dependencyGraph.addChildrenToRoot(dependency);
            }

            lastLevel = currentLevel;
            dependencyStack.push(dependency);
        }

        if (!dependencyGraph.getRootDependencyExternalIds().isEmpty()) {
            final ExternalId projectExternalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, projectName, projectVersionName);
            final CodeLocation codeLocation = new CodeLocation.Builder(CodeLocationType.PIP, dependencyGraph, projectExternalId).build();
            return new PipParseResult(projectName, projectVersionName, codeLocation);
        } else {
            return null;
        }
    }

    public int getLevel(final String line) {
        String consumableLine = line;
        int level = 0;

        while (consumableLine.startsWith(DEPENDENCY_INDENTATION)) {
            consumableLine = consumableLine.replaceFirst(DEPENDENCY_INDENTATION, "");
            level++;
        }

        return level;
    }

    public Optional<Dependency> getDependencyFromLine(final Map<String, String[]> pipFreezeMap, final String line) {
        Dependency dependency = null;
        String name = null;
        String version = null;
        final String trimmedLine = line.trim();

        if (line.contains(DEPENDENCY_NAME_PREFIX) && line.contains(DEPENDENCY_NAME_SUFFIX) && line.contains(DEPENDENCY_VERSION_PREFIX) && line.contains(DEPENDENCY_VERSION_SUFFIX)) {
            name = trimmedLine.substring(trimmedLine.indexOf(DEPENDENCY_NAME_PREFIX) + DEPENDENCY_NAME_PREFIX.length(), trimmedLine.indexOf(DEPENDENCY_NAME_SUFFIX));
            version = trimmedLine.substring(trimmedLine.indexOf(DEPENDENCY_VERSION_PREFIX) + DEPENDENCY_VERSION_PREFIX.length(), trimmedLine.indexOf(DEPENDENCY_VERSION_SUFFIX));
        } else if (trimmedLine.contains(TOP_LEVEL_SEPARATOR)) {
            final String[] splitLine = trimmedLine.split(TOP_LEVEL_SEPARATOR);
            name = splitLine[0];
            version = splitLine[1];
        }

        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(version)) {
            final String[] pipFreezeResult = pipFreezeMap.get(name.toLowerCase());
            if (pipFreezeResult != null) {
                name = pipFreezeResult[0].trim();
                version = pipFreezeResult[1].trim();
            }

            final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, name, version);
            dependency = new Dependency(name, version, externalId);
        }

        return Optional.ofNullable(dependency);
    }

}
