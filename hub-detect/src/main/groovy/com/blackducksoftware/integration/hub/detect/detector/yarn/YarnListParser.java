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
package com.blackducksoftware.integration.hub.detect.detector.yarn;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.util.DependencyHistory;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.hub.bdio.model.Forge;
import com.synopsys.integration.hub.bdio.model.dependency.Dependency;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.util.NameVersion;

public class YarnListParser extends BaseYarnParser {
    private final Logger logger = LoggerFactory.getLogger(YarnListParser.class);
    private final ExternalIdFactory externalIdFactory;
    private final YarnLockParser yarnLockParser;

    public static final String LAST_DEPENDENCY_PREFIX = "\u2514\u2500";
    public static final String NTH_DEPENDENCY_PREFIX = "\u251C\u2500";
    public static final String INNER_LEVEL_CHARACTER = "\u2502";

    public YarnListParser(final ExternalIdFactory externalIdFactory, final YarnLockParser yarnLockParser) {
        this.externalIdFactory = externalIdFactory;
        this.yarnLockParser = yarnLockParser;
    }

    public DependencyGraph parseYarnList(final List<String> yarnLockText, final List<String> yarnListAsList) {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        final DependencyHistory history = new DependencyHistory();

        final Map<String, String> yarnLockVersionMap = yarnLockParser.getYarnLockResolvedVersionMap(yarnLockText);

        for (final String line : yarnListAsList) {
            final String lowerCaseLine = line.toLowerCase().trim();
            final String cleanedLine = line.replaceAll(NTH_DEPENDENCY_PREFIX, "").replaceAll(INNER_LEVEL_CHARACTER, "").replaceAll(LAST_DEPENDENCY_PREFIX, "");
            if (!cleanedLine.contains("@") || lowerCaseLine.startsWith("yarn list") || lowerCaseLine.startsWith("done in") || lowerCaseLine.startsWith("warning")) {
                continue;
            }

            final Dependency dependency = parseDependencyFromLine(cleanedLine, yarnLockVersionMap);
            final int lineLevel = getLineLevel(cleanedLine);
            try {
                history.clearDependenciesDeeperThan(lineLevel);
            } catch (final IllegalStateException e) {
                logger.warn(String.format("Problem parsing line '%s': %s", line, e.getMessage()));
            }

            if (history.isEmpty()) {
                graph.addChildToRoot(dependency);
            } else {
                graph.addChildWithParents(dependency, history.getLastDependency());
            }

            history.add(dependency);
        }

        return graph;
    }

    public Dependency parseDependencyFromLine(final String cleanedLine, final Map<String, String> yarnLockVersionMap) {
        final String fuzzyNameVersionString = cleanedLine.trim();
        String cleanedFuzzyNameVersionString = fuzzyNameVersionString;
        if (fuzzyNameVersionString.startsWith("@")) {
            cleanedFuzzyNameVersionString = fuzzyNameVersionString.substring(1);
        }

        final String[] nameVersionArray = cleanedFuzzyNameVersionString.split("@");
        final NameVersion nameVersion = new NameVersion(nameVersionArray[0], nameVersionArray[1]);
        final String resolvedVersion = yarnLockVersionMap.get(fuzzyNameVersionString);

        if (resolvedVersion != null) {
            nameVersion.setVersion(resolvedVersion);
        }

        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPM, nameVersion.getName(), nameVersion.getVersion());
        return new Dependency(nameVersion.getName(), nameVersion.getVersion(), externalId);
    }

}
