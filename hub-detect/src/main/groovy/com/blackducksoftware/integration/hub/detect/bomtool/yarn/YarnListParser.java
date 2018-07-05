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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;

public class YarnListParser extends BaseYarnParser {
    private final Logger logger = LoggerFactory.getLogger(YarnListParser.class);

    public DependencyGraph parseYarnList(final List<String> yarnLockText, final List<String> yarnListAsList) {
        final YarnDependencyMapper yarnDependencyMapper = new YarnDependencyMapper();
        yarnDependencyMapper.getYarnDataAsMap(yarnLockText);

        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        final ExternalId extId = new ExternalId(Forge.NPM);
        final UUID randomUUID = UUID.randomUUID();
        final String rootName = String.format("detectRootNode - %s", randomUUID);
        extId.name = rootName;

        int depth;
        Dependency parentDep = null;
        for (final String line : yarnListAsList) {

            if (line.toLowerCase().startsWith("yarn list") || line.toLowerCase().startsWith("done in") || line.toLowerCase().startsWith("warning")) {
                continue;
            }

            final String cleanedLine = line.replaceAll("├─", "").replaceAll("│", "").replaceAll("└─", "");
            depth = getLineLevel(cleanedLine);

            if (depth == 0) {
                final Optional<Dependency> optionalDependency = getDependencyFromLine(cleanedLine, yarnDependencyMapper);
                if (optionalDependency.isPresent()) {
                    final Dependency currentDep = optionalDependency.get();
                    graph.addChildToRoot(currentDep);
                    parentDep = currentDep;
                } else {
                    continue;
                }
            }

            if (depth >= 1) {
                final Optional<Dependency> optionalDependency = getDependencyFromLine(cleanedLine, yarnDependencyMapper);
                if (optionalDependency.isPresent()) {
                    final Dependency currentDep = optionalDependency.get();
                    if (parentDep != null) {
                        logger.debug(currentDep.name + "@" + currentDep.version + " is being added as a child of " + parentDep.name + "@" + parentDep.version);
                        graph.addChildWithParent(currentDep, parentDep);
                    } else {
                        logger.debug(
                                String.format("Problem parsing dependency %s@%s: Depth is %s, but no parent could be found. Treating dependency as root level to avoid missing dependencies.", currentDep.name, currentDep.version, depth));
                        graph.addChildToRoot(currentDep);
                    }
                } else {
                    continue;
                }
            }
        }

        return graph;
    }

    private Optional<Dependency> getDependencyFromLine(final String cleanedLine, final YarnDependencyMapper yarnDependencyMapper) {
        final String fuzzyName = cleanedLine.trim();
        final Optional<String> optionalName = parseNameFromFuzzy(fuzzyName);
        final Optional<String> optionalVersion = yarnDependencyMapper.getVersion(fuzzyName);

        if (optionalName.isPresent() && optionalVersion.isPresent()) {
            final String name = optionalName.get();
            final String version = optionalVersion.get();
            logger.debug("Found version " + version + " for " + fuzzyName);

            final ExternalId extId = new ExternalId(Forge.NPM);
            extId.name = name;
            extId.version = version;

            return Optional.of(new Dependency(name, version, extId));
        } else {
            if (!optionalName.isPresent()) {
                logger.error(String.format("Could not determine a name for yarn dependency %s", fuzzyName));
            }
            if (!optionalVersion.isPresent()) {
                logger.error(String.format("Could not determine a version for yarn dependency %s", fuzzyName));
            }
            return Optional.empty();
        }
    }

    private Optional<String> parseNameFromFuzzy(final String fuzzyName) {
        if (StringUtils.isBlank(fuzzyName)) {
            return Optional.empty();
        }
        String name = null;
        if (fuzzyName.startsWith("@")) {
            final String fuzzyNameWithoutFirstAt = fuzzyName.substring(1);
            name = fuzzyName.substring(0, fuzzyNameWithoutFirstAt.indexOf("@") + 1);
        } else {
            name = fuzzyName.substring(0, fuzzyName.indexOf("@"));
        }
        return Optional.of(name);
    }
}
