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
package com.blackducksoftware.integration.hub.detect.bomtool.rubygems.parse;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependencyid.DependencyId;
import com.blackducksoftware.integration.hub.bdio.model.dependencyid.NameDependencyId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.util.NameVersion;

public class GemlockParser {
    private final Logger logger = LoggerFactory.getLogger(GemlockParser.class);

    private final ExternalIdFactory externalIdFactory;
    private LazyExternalIdDependencyGraphBuilder lazyBuilder;
    private HashSet<String> directDependencyNames;
    private DependencyId currentParent;

    private boolean inSpecsSection = false;
    private boolean inDependenciesSection = false;
    private boolean previousLineWasBundledWith = false;

    public GemlockParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parseProjectDependencies(final List<String> gemfileLockLines) {
        lazyBuilder = new LazyExternalIdDependencyGraphBuilder();
        directDependencyNames = new HashSet<>();
        currentParent = null;

        gemfileLockLines.forEach(line -> {
            if (StringUtils.isBlank(line)) {
                inSpecsSection = false;
                inDependenciesSection = false;
                return;
            }

            if (!inSpecsSection && "specs:".equals(line.trim())) {
                inSpecsSection = true;
                return;
            }

            if (!inDependenciesSection && "DEPENDENCIES".equals(line.trim())) {
                inDependenciesSection = true;
                return;
            }

            if ("BUNDLED WITH".equals(line.trim())) {
                previousLineWasBundledWith = true;
            } else if (previousLineWasBundledWith) {
                previousLineWasBundledWith = false;
                final String name = "bundler";
                final String version = line.trim();
                final DependencyId bundlerId = new NameDependencyId(name);
                lazyBuilder.setDependencyInfo(bundlerId, name, version, externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, name, version));
            }

            if (!inSpecsSection && !inDependenciesSection) {
                return;
            }

            // we are now either in the specs section or in the dependencies section
            if (inSpecsSection) {
                parseSpecsSectionLine(line);
            } else {
                parseDependencySectionLine(line);
            }
        });

        directDependencyNames.forEach(directDependencyName -> {
            lazyBuilder.addChildToRoot(new NameDependencyId(directDependencyName));
        });

        return lazyBuilder.build();
    }

    private void parseSpecsSectionLine(final String line) {
        if (line.startsWith("      ")) {
            parseSpecRelationshipLine(line);
        } else if (line.startsWith("    ")) {
            parseSpecPackageLine(line);
        } else {
            logger.error("Line in specs section can't be parsed: " + line);
        }
    }

    private void parseSpecRelationshipLine(final String line) {
        if (currentParent == null) {
            logger.error("Trying to add a child without a parent: " + line);
        } else {
            final NameVersion childNode = parseNameVersion(line);
            final DependencyId childId = new NameDependencyId(childNode.getName());
            lazyBuilder.addChildWithParent(childId, currentParent);
        }
    }

    private void parseSpecPackageLine(final String line) {
        final NameVersion parentNameVersion = parseNameVersion(line);
        if (parentNameVersion.getVersion() != null) {
            currentParent = new NameDependencyId(parentNameVersion.getName());
            final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, parentNameVersion.getName(), parentNameVersion.getVersion());
            lazyBuilder.setDependencyInfo(currentParent, parentNameVersion.getName(), parentNameVersion.getVersion(), externalId);
        } else {
            logger.error("An installed spec did not have a non-fuzzy version: " + line);
        }
    }

    private void parseDependencySectionLine(final String line) {
        final NameVersion dependencyNameVersionNode = parseNameVersion(line);
        if (dependencyNameVersionNode.getName() == null) {
            logger.error("Line in dependencies section can't be parsed: " + line);
        } else {
            lazyBuilder.addChildToRoot(new NameDependencyId(dependencyNameVersionNode.getName()));
        }
    }

    private NameVersion parseNameVersion(final String line) {
        final String[] pieces = line.trim().split(" ");
        String name = pieces[0].trim();
        String version = "";
        if (pieces.length > 1) {
            final Optional<String> validVersion = parseValidVersion(pieces[1].trim());
            version = validVersion.orElse("");
        }

        if (name.endsWith("!")) {
            name = name.substring(0, name.length() - 2);
        }
        return new NameVersion(name, version);
    }

    // a valid version looks like (###.###.###)
    private Optional<String> parseValidVersion(final String version) {
        final String firstChar = version.substring(0, 1);
        final String lastChar = version.substring(version.length() - 1);
        if (firstChar.equals("(") && lastChar.equals(")") && isNotFuzzy(version)) {
            return Optional.of(version.substring(1, version.length() - 1));
        } else {
            return Optional.empty();
        }
    }

    private boolean isNotFuzzy(final String version) {
        final boolean containsFuzzyIndicator = version.indexOf("=") > 0 || version.indexOf("~") >= 0 || version.indexOf(">") >= 0 || version.indexOf("<") >= 0;
        return !containsFuzzyIndicator;
    }

}
