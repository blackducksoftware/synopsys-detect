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
package com.blackducksoftware.integration.hub.detect.bomtool.yarn;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.util.NameVersion;

public class YarnListParser extends BaseYarnParser {
    private final Logger logger = LoggerFactory.getLogger(YarnListParser.class);
    private final ExternalIdFactory externalIdFactory;

    public static final String LAST_DEPENDENCY_PREFIX = "\u2514\u2500";
    public static final String NTH_DEPENDENCY_PREFIX = "\u251C\u2500";
    public static final String INNER_LEVEL_CHARACTER = "\u2502";

    public YarnListParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parseYarnList(final List<String> yarnLockText, final List<String> yarnListAsList) {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        final Deque<Dependency> dependencyStack = new LinkedList<>();
        final YarnLockParser yarnLockParser = new YarnLockParser();
        int previousDepth = 0;
        Dependency previousDependency = null;

        final Map<String, String> yarnLockVersionMap = yarnLockParser.getYarnLockResolvedVersionMap(yarnLockText);

        for (final String line : yarnListAsList) {
            final String lowerCaseLine = line.toLowerCase().trim();
            if (StringUtils.isBlank(line) || lowerCaseLine.startsWith("yarn list") || lowerCaseLine.startsWith("done in") || lowerCaseLine.startsWith("warning")) {
                continue;
            }

            final String cleanedLine = line.replaceAll(NTH_DEPENDENCY_PREFIX, "").replaceAll(INNER_LEVEL_CHARACTER, "").replaceAll(LAST_DEPENDENCY_PREFIX, "");
            final Dependency currentDependency = parseDependencyFromLine(cleanedLine, yarnLockVersionMap);
            final int currentDepth = getLineLevel(cleanedLine);

            if (currentDepth == previousDepth + 1 && previousDependency != null) {
                dependencyStack.push(previousDependency);
            } else if (currentDepth < previousDepth) {
                final int depthDelta = (previousDepth - currentDepth);
                for (int levels = 0; levels < depthDelta; levels++) {
                    dependencyStack.pop();
                }
            } else if (currentDepth != previousDepth) {
                logger.error(String.format("The tree level (%s) and this line (%s) with depth %s can\'t be reconciled.", previousDepth, line, currentDepth));
            }

            if (dependencyStack.isEmpty()) {
                graph.addChildToRoot(currentDependency);
            } else {
                graph.addChildWithParents(currentDependency, dependencyStack.peek());
            }

            previousDependency = currentDependency;
            previousDepth = currentDepth;
        }

        return graph;
    }

    private Dependency parseDependencyFromLine(final String cleanedLine, final Map<String, String> yarnLockVersionMap) {
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

    Optional<String> parseNameFromFuzzy(final String fuzzyName) {
        String name = null;
        if (StringUtils.isNotBlank(fuzzyName)) {
            if (fuzzyName.startsWith("@")) {
                final String fuzzyNameWithoutFirstAt = fuzzyName.substring(1);
                name = fuzzyName.substring(0, fuzzyNameWithoutFirstAt.indexOf("@") + 1);
            } else {
                name = fuzzyName.substring(0, fuzzyName.indexOf("@"));
            }
        }
        return Optional.ofNullable(name);
    }
}
