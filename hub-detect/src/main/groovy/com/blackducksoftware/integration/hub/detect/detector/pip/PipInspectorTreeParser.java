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
package com.blackducksoftware.integration.hub.detect.detector.pip;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.util.DependencyHistory;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocationType;
import com.synopsys.integration.hub.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.hub.bdio.model.Forge;
import com.synopsys.integration.hub.bdio.model.dependency.Dependency;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class PipInspectorTreeParser {
    private final Logger logger = LoggerFactory.getLogger(PipInspectorTreeParser.class);

    public static final String SEPARATOR = "==";
    public static final String UNKNOWN_PROJECT_NAME = "n?";
    public static final String UNKNOWN_PROJECT_VERSION = "v?";
    public static final String UNKNOWN_REQUIREMENTS_PREFIX = "r?";
    public static final String UNPARSEABLE_REQUIREMENTS_PREFIX = "p?";
    public static final String UNKNOWN_PACKAGE_PREFIX = "--";
    public static final String INDENTATION = "    ";

    private final ExternalIdFactory externalIdFactory;

    public PipInspectorTreeParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public Optional<PipParseResult> parse(final List<String> pipInspectorOutputAsList, final String sourcePath) {
        PipParseResult parseResult = null;

        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        final DependencyHistory history = new DependencyHistory();
        Dependency project = null;

        for (final String line : pipInspectorOutputAsList) {
            final String trimmedLine = StringUtils.trimToEmpty(line);
            if (StringUtils.isEmpty(trimmedLine) || !trimmedLine.contains(SEPARATOR) || trimmedLine.startsWith(UNKNOWN_REQUIREMENTS_PREFIX) || trimmedLine.startsWith(UNPARSEABLE_REQUIREMENTS_PREFIX) || trimmedLine.startsWith(
                UNKNOWN_PACKAGE_PREFIX)) {
                parseErrorsFromLine(trimmedLine);
                continue;
            }

            final Dependency currentDependency = parseDependencyFromLine(trimmedLine, sourcePath);
            final int lineLevel = getLineLevel(trimmedLine);
            try {
                history.clearDependenciesDeeperThan(lineLevel);
            } catch (final IllegalStateException e) {
                logger.warn(String.format("Problem parsing line '%s': %s", line, e.getMessage()));
            }

            if (project == null) {
                project = currentDependency;
            } else if (project.equals(history.getLastDependency())) {
                graph.addChildToRoot(currentDependency);
            } else if (history.isEmpty()) {
                graph.addChildToRoot(currentDependency);
            } else {
                graph.addChildWithParents(currentDependency, history.getLastDependency());
            }

            history.add(currentDependency);
        }

        if (project != null) {
            final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(DetectCodeLocationType.PIP, sourcePath, project.externalId, graph).build();
            parseResult = new PipParseResult(project.name, project.version, codeLocation);
        }

        return Optional.ofNullable(parseResult);
    }

    private void parseErrorsFromLine(final String trimmedLine) {
        if (trimmedLine.startsWith(UNKNOWN_REQUIREMENTS_PREFIX)) {
            logger.error("Pip inspector could not find requirements file @ " + trimmedLine.substring(UNKNOWN_REQUIREMENTS_PREFIX.length()));
        }

        if (trimmedLine.startsWith(UNPARSEABLE_REQUIREMENTS_PREFIX)) {
            logger.error("Pip inspector could not parse requirements file @ " + trimmedLine.substring(UNPARSEABLE_REQUIREMENTS_PREFIX.length()));
        }

        if (trimmedLine.startsWith(UNKNOWN_PACKAGE_PREFIX)) {
            logger.error("Pip inspector could not resolve the package: " + trimmedLine.substring(UNKNOWN_PACKAGE_PREFIX.length()));
        }
    }

    private Dependency parseDependencyFromLine(final String line, final String sourcePath) {
        final String[] segments = line.split(SEPARATOR);

        String name = segments[0].trim();
        String version = segments[1].trim();
        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, name, version);

        if (name.equals(UNKNOWN_PROJECT_NAME) || version.equals(UNKNOWN_PROJECT_VERSION)) {
            externalId = externalIdFactory.createPathExternalId(Forge.PYPI, sourcePath);
        }

        name = name.equals(UNKNOWN_PROJECT_NAME) ? "" : name;
        version = version.equals(UNKNOWN_PROJECT_VERSION) ? "" : version;

        return new Dependency(name, version, externalId);
    }

    private int getLineLevel(final String line) {
        int level = 0;
        String tmpLine = line;
        while (tmpLine.startsWith(INDENTATION)) {
            tmpLine = tmpLine.replaceFirst(INDENTATION, "");
            level++;
        }

        return level;
    }
}
