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
package com.synopsys.integration.detectable.detectables.rubygems.gemlock.parse;

import static com.synopsys.integration.detectable.detectables.rubygems.gemlock.parse.GemlockParser.GemfileLockSection.BUNDLED_WITH;
import static com.synopsys.integration.detectable.detectables.rubygems.gemlock.parse.GemlockParser.GemfileLockSection.DEPENDENCIES;
import static com.synopsys.integration.detectable.detectables.rubygems.gemlock.parse.GemlockParser.GemfileLockSection.NONE;
import static com.synopsys.integration.detectable.detectables.rubygems.gemlock.parse.GemlockParser.GemfileLockSection.SPECS;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependencyid.DependencyId;
import com.synopsys.integration.bdio.model.dependencyid.NameDependencyId;
import com.synopsys.integration.bdio.model.dependencyid.NameVersionDependencyId;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.util.NameVersion;

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

    private List<String> encounteredDependencies = new ArrayList<>();
    private List<String> resolvedDependencies = new ArrayList<>();

    public GemlockParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parseProjectDependencies(final List<String> gemfileLockLines) throws MissingExternalIdException {
        encounteredDependencies = new ArrayList<>();
        resolvedDependencies = new ArrayList<>();
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

        final List<String> missingDependencies = encounteredDependencies.stream().filter(it -> !resolvedDependencies.contains(it)).collect(Collectors.toList());
        for (final String missingName : missingDependencies) {
            final String missingVersion = "";
            final DependencyId dependencyId = new NameDependencyId(missingName);
            final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, missingName, missingVersion);
            lazyBuilder.setDependencyInfo(dependencyId, missingName, missingVersion, externalId);
        }

        return lazyBuilder.build();
    }

    private void discoveredDependencyInfo(final NameVersionDependencyId id) {
        final NameDependencyId nameOnlyId = new NameDependencyId(id.getName());

        //regardless we found the external id for this specific dependency.
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, id.getName(), id.getVersion());
        lazyBuilder.setDependencyInfo(id, id.getName(), id.getVersion(), externalId);

        if (!resolvedDependencies.contains(id.getName())) { //if this is our first time encountering a dependency of this name, we become the 'version-less'
            resolvedDependencies.add(id.getName());
            lazyBuilder.setDependencyInfo(nameOnlyId, id.getName(), id.getVersion(), externalId);
        } else {//otherwise, add us as a child to the version-less
            lazyBuilder.addChildWithParent(id, nameOnlyId);
        }

    }

    private void addBundlerDependency(final String trimmedLine) {
        final NameVersionDependencyId bundlerId = new NameVersionDependencyId("bundler", trimmedLine);
        discoveredDependencyInfo(bundlerId);
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
            final NameVersion childNameVersion = parseNameVersion(trimmedLine);
            final DependencyId childId = processNameVersion(childNameVersion);
            lazyBuilder.addChildWithParent(childId, currentParent);
        }
    }

    private void parseSpecPackageLine(final String trimmedLine) {
        final NameVersion parentNameVersion = parseNameVersion(trimmedLine);
        if (StringUtils.isNotBlank(parentNameVersion.getVersion())) {
            currentParent = new NameDependencyId(parentNameVersion.getName());
            discoveredDependencyInfo(new NameVersionDependencyId(parentNameVersion.getName(), parentNameVersion.getVersion()));
        } else {
            logger.error(String.format("An installed spec did not have a non-fuzzy version: %s", trimmedLine));
        }
    }

    //If you have Version, you know everything. Otherwise, you need to find this version later.
    //Generally each parse/process call should either call this or add to encountered.
    private DependencyId processNameVersion(final NameVersion nameVersion) {
        final NameDependencyId nameDependencyId = new NameDependencyId(nameVersion.getName());
        if (StringUtils.isNotBlank(nameVersion.getVersion())) {
            final NameVersionDependencyId nameVersionDependencyId = new NameVersionDependencyId(nameVersion.getName(), nameVersion.getVersion());
            discoveredDependencyInfo(nameVersionDependencyId);
        } else {
            encounteredDependencies.add(nameVersion.getName());
        }
        return nameDependencyId;
    }

    private void parseDependencySectionLine(final String trimmedLine) {
        final NameVersion dependencyNameVersionNode = parseNameVersion(trimmedLine);
        if (dependencyNameVersionNode.getName() == null) {
            logger.error(String.format("Line in dependencies section can't be parsed: %s", trimmedLine));
        } else {
            final DependencyId dependencyId = processNameVersion(dependencyNameVersionNode);
            lazyBuilder.addChildToRoot(dependencyId);
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
        SPECS
    }

}
