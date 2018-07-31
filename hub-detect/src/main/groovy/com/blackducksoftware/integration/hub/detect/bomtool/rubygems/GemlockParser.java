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
package com.blackducksoftware.integration.hub.detect.bomtool.rubygems;

import static com.blackducksoftware.integration.hub.detect.bomtool.rubygems.GemlockParser.GemfileLockSection.BUNDLED_WITH;
import static com.blackducksoftware.integration.hub.detect.bomtool.rubygems.GemlockParser.GemfileLockSection.DEPENDENCIES;
import static com.blackducksoftware.integration.hub.detect.bomtool.rubygems.GemlockParser.GemfileLockSection.NONE;
import static com.blackducksoftware.integration.hub.detect.bomtool.rubygems.GemlockParser.GemfileLockSection.SPECS;

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
    public static final String DEPENDENCIES_HEADER = "DEPENDENCIES";
    public static final String BUNDLED_WITH_HEADER = "BUNDLED WITH";
    public static final String SPECS_HEADER = "specs:";

    public static final String SPEC_RELATIONSHIP_PREFIX = "      ";
    public static final String SPEC_PACKAGE_PREFIX = "    ";

    public static final String VERSION_CHARACTERS = "()<>=~";
    public static final String FUZZY_VERSION_CHARACTERS = "<>";
    public static final String VERSION_PREFIX_PATTERN = " \\(";
    public static final String VERSION_SUFFIX = ")";

    private final Logger logger = LoggerFactory.getLogger(GemlockParser.class);

    private final ExternalIdFactory externalIdFactory;
    private LazyExternalIdDependencyGraphBuilder lazyBuilder;
    private DependencyId currentParent;

    private GemfileLockSection currentSection = NONE;

    public GemlockParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parseProjectDependencies(final List<String> gemfileLockLines) {
        lazyBuilder = new LazyExternalIdDependencyGraphBuilder();
        currentParent = null;

        for (final String line : gemfileLockLines) {
            final String trimmedLine = StringUtils.trimToEmpty(line);

            if (StringUtils.isBlank(trimmedLine)) {
                currentSection = NONE;
            } else if (SPECS_HEADER.equals(trimmedLine)) {
                currentSection = SPECS;
            } else if (DEPENDENCIES_HEADER.equals(trimmedLine)) {
                currentSection = DEPENDENCIES;
            } else if (BUNDLED_WITH_HEADER.equals(trimmedLine)) {
                currentSection = BUNDLED_WITH;
            } else if (BUNDLED_WITH.equals(currentSection)) {
                addBundlerDependency(trimmedLine);
            } else if (SPECS.equals(currentSection)) {
                parseSpecsSectionLine(line);
            } else if (DEPENDENCIES.equals(currentSection)) {
                parseDependencySectionLine(trimmedLine);
            }
        }

        return lazyBuilder.build();
    }

    private void addBundlerDependency(final String trimmedLine) {
        final String name = "bundler";
        final String version = trimmedLine;
        final DependencyId bundlerId = new NameDependencyId(name);
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, name, version);
        lazyBuilder.setDependencyInfo(bundlerId, name, version, externalId);
    }

    private void parseSpecsSectionLine(final String untrimmedLine) {
        if (untrimmedLine.startsWith(SPEC_RELATIONSHIP_PREFIX)) {
            parseSpecRelationshipLine(untrimmedLine.trim());
        } else if (untrimmedLine.startsWith(SPEC_PACKAGE_PREFIX)) {
            parseSpecPackageLine(untrimmedLine.trim());
        } else {
            logger.error(String.format("Line in specs section can't be parsed: %s", untrimmedLine));
        }
    }

    private void parseSpecRelationshipLine(final String trimmedLine) {
        if (currentParent == null) {
            logger.error(String.format("Trying to add a child without a parent: %s", trimmedLine));
        } else {
            final NameVersion childNode = parseNameVersion(trimmedLine);
            final DependencyId childId = new NameDependencyId(childNode.getName());
            lazyBuilder.addChildWithParent(childId, currentParent);
        }
    }

    private void parseSpecPackageLine(final String trimmedLine) {
        final NameVersion parentNameVersion = parseNameVersion(trimmedLine);
        if (parentNameVersion.getVersion() != null) {
            currentParent = new NameDependencyId(parentNameVersion.getName());
            final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, parentNameVersion.getName(), parentNameVersion.getVersion());
            lazyBuilder.setDependencyInfo(currentParent, parentNameVersion.getName(), parentNameVersion.getVersion(), externalId);
        } else {
            logger.error(String.format("An installed spec did not have a non-fuzzy version: %s", trimmedLine));
        }
    }

    private void parseDependencySectionLine(final String trimmedLine) {
        final NameVersion dependencyNameVersionNode = parseNameVersion(trimmedLine);
        if (dependencyNameVersionNode.getName() == null) {
            logger.error(String.format("Line in dependencies section can't be parsed: %s", trimmedLine));
        } else {
            lazyBuilder.addChildToRoot(new NameDependencyId(dependencyNameVersionNode.getName()));
        }
    }

    private NameVersion parseNameVersion(final String trimmedLine) {
        final String[] pieces = trimmedLine.split(VERSION_PREFIX_PATTERN);
        String name = pieces[0].trim();
        String version = "";

        if (pieces.length > 1) {
            final Optional<String> validVersion = parseValidVersion(pieces[1].trim());
            version = validVersion.orElse("");
        }

        if (name.endsWith("!")) {
            name = name.substring(0, name.length() - 1);
        }

        return new NameVersion(name, version);
    }

    // a valid version looks like (###.###.###)
    private Optional<String> parseValidVersion(final String version) {
        String validVersion = null;

        if (version.endsWith(VERSION_SUFFIX) && StringUtils.containsNone(version, FUZZY_VERSION_CHARACTERS)) {
            validVersion = StringUtils.replaceChars(version, VERSION_CHARACTERS, "").trim();
        }

        return Optional.ofNullable(validVersion);
    }

    enum GemfileLockSection {
        BUNDLED_WITH,
        DEPENDENCIES,
        NONE,
        SPECS;
    }

}
