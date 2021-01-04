/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.rebar.parse;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.util.DependencyHistory;
import com.synopsys.integration.detectable.detectables.rebar.model.RebarParseResult;
import com.synopsys.integration.util.NameVersion;

public class Rebar3TreeParser {
    private final Logger logger = LoggerFactory.getLogger(Rebar3TreeParser.class);

    private static final String LAST_DEPENDENCY_CHARACTER = "\u2514";
    private static final String NTH_DEPENDENCY_CHARACTER = "\u251C";
    private static final String HORIZONTAL_SEPARATOR_CHARACTER = "\u2500";
    private static final String INNER_LEVEL_CHARACTER = "\u2502";
    private static final String INNER_LEVEL_PREFIX = INNER_LEVEL_CHARACTER + "  ";
    private static final String OUTER_LEVEL_PREFIX = "   ";
    private static final String PROJECT_IDENTIFIER = "(project app)";

    private final ExternalIdFactory externalIdFactory;

    public Rebar3TreeParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public RebarParseResult parseRebarTreeOutput(final List<String> dependencyTreeOutput) {
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
                history.clearDependenciesDeeperThan(lineLevel);
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
            final CodeLocation codeLocation = new CodeLocation(graph);
            return new RebarParseResult(codeLocation);
        } else {
            final CodeLocation codeLocation = new CodeLocation(graph, project.getExternalId());
            return new RebarParseResult(new NameVersion(project.getName(), project.getVersion()), codeLocation);
        }
    }

    public Dependency createDependencyFromLine(final String line) {
        final String nameVersionLine = reduceLineToNameVersion(line);
        final String name = nameVersionLine.substring(0, nameVersionLine.lastIndexOf(HORIZONTAL_SEPARATOR_CHARACTER));
        final String version = nameVersionLine.substring(nameVersionLine.lastIndexOf(HORIZONTAL_SEPARATOR_CHARACTER) + 1);
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.HEX, name, version);

        return new Dependency(name, version, externalId);
    }

    public String reduceLineToNameVersion(String line) {
        final List<String> ignoredSpecialCharacters = Arrays.asList(LAST_DEPENDENCY_CHARACTER, NTH_DEPENDENCY_CHARACTER, INNER_LEVEL_CHARACTER);
        for (final String specialCharacter : ignoredSpecialCharacters) {
            line = line.replaceAll(specialCharacter, "");
        }

        line = line.replaceFirst(HORIZONTAL_SEPARATOR_CHARACTER, "");

        if (line.endsWith(")")) {
            line = line.substring(0, line.lastIndexOf('('));
        }

        return line.trim();
    }

    public int getDependencyLevelFromLine(String line) {
        int level = 0;
        while (line.startsWith(INNER_LEVEL_PREFIX) || line.startsWith(OUTER_LEVEL_PREFIX)) {
            line = line.substring(3);
            level++;
        }

        return level;
    }

    public boolean isProject(final String line) {
        String forgeString = "";
        if (line.endsWith(")")) {
            forgeString = line.substring(line.lastIndexOf('('));
        }

        return PROJECT_IDENTIFIER.equals(forgeString);
    }
}
