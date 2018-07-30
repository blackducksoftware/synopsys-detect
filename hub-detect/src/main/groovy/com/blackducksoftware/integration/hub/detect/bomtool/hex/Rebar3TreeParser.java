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
import com.blackducksoftware.integration.hub.detect.util.DependencyHistory;
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
        final DependencyHistory history = new DependencyHistory();
        Dependency project = null;

        for (final String line : dependencyTreeOutput) {
            if (!line.contains(HORIZONTAL_SEPARATOR_CHARACTER)) {
                continue;
            }

            final Dependency currentDependency = createDependencyFromLine(line);
            final int lineLevel = getDependencyLevelFromLine(line);
            try {
                history.clearHistoryPast(lineLevel);
            } catch (final IllegalStateException e) {
                logger.warn(String.format("Problem parsing line '%s': %s", line, e.getMessage()));
            }

            if (history.isEmpty() && isProject(line)) {
                project = currentDependency;
            } else if (history.getLastDependency().equals(project)) {
                graph.addChildToRoot(currentDependency);
            } else if (history.isEmpty()) {
                graph.addChildToRoot(currentDependency);
            } else {
                graph.addChildWithParents(currentDependency, history.getLastDependency());
            }

            history.add(currentDependency);
        }

        if (project == null) {
            final ExternalId projectExternalId = externalIdFactory.createPathExternalId(Forge.HEX, sourcePath);
            project = new Dependency("", "", projectExternalId);
        }

        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.HEX, project.name, project.version);
        final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(BomToolGroupType.HEX, bomToolType, sourcePath, externalId, graph).build();
        return new RebarParseResult(project.name, project.version, codeLocation);
    }

    protected Dependency createDependencyFromLine(final String line) {
        final String nameVersionLine = reduceLineToNameVersion(line);
        final String name = nameVersionLine.substring(0, nameVersionLine.lastIndexOf(HORIZONTAL_SEPARATOR_CHARACTER));
        final String version = nameVersionLine.substring(nameVersionLine.lastIndexOf(HORIZONTAL_SEPARATOR_CHARACTER) + 1);
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.HEX, name, version);

        return new Dependency(name, version, externalId);
    }

    protected String reduceLineToNameVersion(String line) {
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

    protected int getDependencyLevelFromLine(String line) {
        int level = 0;
        while (line.startsWith(INNER_LEVEL_PREFIX) || line.startsWith(OUTER_LEVEL_PREFIX)) {
            line = line.substring(3);
            level++;
        }

        return level;
    }

    protected boolean isProject(final String line) {
        String forgeString = "";
        if (line.endsWith(")")) {
            forgeString = line.substring(line.lastIndexOf("("));
        }

        return PROJECT_IDENTIFIER.equals(forgeString);
    }
}
