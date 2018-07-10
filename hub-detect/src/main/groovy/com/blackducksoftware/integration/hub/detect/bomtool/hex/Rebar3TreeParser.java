/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.bomtool.hex;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;

public class Rebar3TreeParser {
    private final Logger logger = LoggerFactory.getLogger(Rebar3TreeParser.class);

    public static final String LAST_DEPENDENCY_CHARACTER = "\u2514";
    public static final String NTH_DEPENDENCY_CHARACTER = "\u251C";
    public static final String HORIZONTAL_SEPARATOR_CHARACTER = "\u2500";
    public static final String INNER_LEVEL_CHARACTER = "\u2502";
    public static final String INNER_LEVEL_PREFIX = INNER_LEVEL_CHARACTER + "  ";
    public static final String OUTER_LEVEL_PREFIX = "   ";
    public static final String PROJECT_IDENTIFIER = "(project app)";

    private final ExternalIdFactory externalIdFactory;

    public Rebar3TreeParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public RebarParseResult parseRebarTreeOutput(final BomToolType bomToolType, final List<String> dependencyTreeOutput, final String sourcePath) {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        String projectName = "";
        String projectVersionName = "";
        final Stack<Dependency> dependencyStack = new Stack<>();
        int previousTreeLevel = 0;
        Dependency previousDependency = null;

        try {
            for (final String line : dependencyTreeOutput) {
                if (line.contains(HORIZONTAL_SEPARATOR_CHARACTER)) {
                    final Dependency currentDependency = createDependencyFromLine(line);

                    final int currentTreeLevel = getDependencyLevelFromLine(line);

                    if (currentTreeLevel == previousTreeLevel + 1 && previousDependency != null) {
                        dependencyStack.push(previousDependency);
                    } else if (currentTreeLevel < previousTreeLevel) {
                        final int levelDelta = (previousTreeLevel - currentTreeLevel);
                        for (int levels = 0; levels < levelDelta; levels++) {
                            dependencyStack.pop();
                        }
                    } else if (currentTreeLevel != previousTreeLevel) {
                        logger.error(String.format("The tree level (%s) and this line (%s) with count %s can\'t be reconciled.", previousTreeLevel, line, currentTreeLevel));
                    }

                    if (dependencyStack.size() == 0) {
                        if (isProject(line)) {
                            projectName = currentDependency.name;
                            projectVersionName = currentDependency.version;
                        } else {
                            graph.addChildToRoot(currentDependency);
                        }
                    } else if (dependencyStack.size() == 1 && dependencyStack.peek().name.equals(projectName) && dependencyStack.peek().version.equals(projectVersionName)) {
                        graph.addChildToRoot(currentDependency);
                    } else {
                        graph.addChildWithParents(currentDependency, dependencyStack.peek());
                    }

                    previousDependency = currentDependency;
                    previousTreeLevel = currentTreeLevel;
                }
            }
        } catch (final Exception e) {
            logger.error("Exception parsing rebar output: " + e.getMessage());
        }

        final ExternalId id = externalIdFactory.createNameVersionExternalId(Forge.HEX, projectName, projectVersionName);

        final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(BomToolGroupType.HEX, bomToolType, sourcePath, id, graph).build();
        final RebarParseResult result = new RebarParseResult(projectName, projectVersionName, codeLocation);
        return result;
    }

    Dependency createDependencyFromLine(final String line) {

        final String nameVersionLine = reduceLineToNameVersion(line);
        final String name = nameVersionLine.substring(0, nameVersionLine.lastIndexOf(HORIZONTAL_SEPARATOR_CHARACTER));
        final String version = nameVersionLine.substring(nameVersionLine.lastIndexOf(HORIZONTAL_SEPARATOR_CHARACTER) + 1);
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.HEX, name, version);

        return new Dependency(name, version, externalId);
    }

    String reduceLineToNameVersion(String line) {

        final List<String> ignoredSpecialCharacters = Arrays.asList(LAST_DEPENDENCY_CHARACTER, NTH_DEPENDENCY_CHARACTER, INNER_LEVEL_CHARACTER);
        for (final String specialCharacter : ignoredSpecialCharacters) {
            line = line.replaceAll(specialCharacter, "");
        }

        line = line.replaceFirst(HORIZONTAL_SEPARATOR_CHARACTER, "");

        if (line.endsWith(")")) {
            line = line.substring(0, line.lastIndexOf("("));
        }

        return line.trim();
    }

    int getDependencyLevelFromLine(String line) {
        int level = 0;
        while (line.startsWith(INNER_LEVEL_PREFIX) || line.startsWith(OUTER_LEVEL_PREFIX)) {
            line = line.substring(3);
            level++;
        }

        return level;
    }

    boolean isProject(final String line) {
        String forgeString = "";
        if (line.endsWith(")")) {
            forgeString = line.substring(line.lastIndexOf("("));
        }

        return PROJECT_IDENTIFIER.equals(forgeString);
    }
}
