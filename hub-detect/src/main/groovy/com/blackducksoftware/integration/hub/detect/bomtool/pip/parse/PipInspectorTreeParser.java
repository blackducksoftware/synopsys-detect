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
package com.blackducksoftware.integration.hub.detect.bomtool.pip.parse;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.model.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

public class PipInspectorTreeParser {
    final Logger logger = LoggerFactory.getLogger(PipInspectorTreeParser.class);

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

    public PipParseResult parse(final String treeText, final String sourcePath) {
        final List<String> lines = Arrays.asList(treeText.trim().split(System.lineSeparator()));

        MutableMapDependencyGraph dependencyGraph = null;
        final Stack<Dependency> tree = new Stack<>();

        Dependency project = null;

        int indentation = 0;
        for (final String line : lines) {
            if (StringUtils.trimToNull(line) == null) {
                continue;
            }

            if (line.trim().startsWith(UNKNOWN_REQUIREMENTS_PREFIX)) {
                final String path = line.replace(UNKNOWN_REQUIREMENTS_PREFIX, "").trim();
                logger.info("Pip inspector could not find requirements file @ " + path);
                continue;
            }

            if (line.trim().startsWith(UNPARSEABLE_REQUIREMENTS_PREFIX)) {
                final String path = line.replace(UNPARSEABLE_REQUIREMENTS_PREFIX, "").trim();
                logger.info("Pip inspector could not parse requirements file @ " + path);
                continue;
            }

            if (line.trim().startsWith(UNKNOWN_PACKAGE_PREFIX)) {
                final String packageName = line.replace(UNKNOWN_PACKAGE_PREFIX, "").trim();
                logger.info("Pip inspector could not resolve the package: " + packageName);
                continue;
            }

            if (line.contains(SEPARATOR) && dependencyGraph == null) {
                dependencyGraph = new MutableMapDependencyGraph();
                project = projectLineToDependency(line, sourcePath);
                continue;
            }

            if (dependencyGraph == null) {
                continue;
            }

            final int currentIndentation = getCurrentIndentation(line);
            final Dependency next = lineToDependency(line);
            if (currentIndentation == indentation) {
                tree.pop();
            } else {
                for (; indentation >= currentIndentation; indentation--) {
                    tree.pop();
                }
            }

            if (tree.size() > 0) {
                dependencyGraph.addChildWithParent(next, tree.peek());
            } else {
                dependencyGraph.addChildrenToRoot(next);
            }

            indentation = currentIndentation;
            tree.push(next);
        }

        if (project != null && !(project.name.equals("") && project.version.equals("") && dependencyGraph != null && dependencyGraph.getRootDependencyExternalIds().isEmpty())) {
            final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(BomToolGroupType.PIP, sourcePath, project.externalId, dependencyGraph).build();
            return new PipParseResult(project.name, project.version, codeLocation);
        } else {
            return null;
        }
    }

    private Dependency projectLineToDependency(final String line, final String sourcePath) {
        if (!line.contains(SEPARATOR)) {
            return null;
        }
        final String[] segments = line.split(SEPARATOR);
        String name = segments[0].trim();
        String version = segments[1].trim();

        ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, name, version);
        if (name.equals(UNKNOWN_PROJECT_NAME) || version.equals(UNKNOWN_PROJECT_VERSION)) {
            externalId = externalIdFactory.createPathExternalId(Forge.PYPI, sourcePath);
        }

        name = name.equals(UNKNOWN_PROJECT_NAME) ? "" : name;
        version = version.equals(UNKNOWN_PROJECT_VERSION) ? "" : version;

        final Dependency node = new Dependency(name, version, externalId);

        return node;
    }

    private Dependency lineToDependency(final String line) {
        if (!line.contains(SEPARATOR)) {
            return null;
        }
        final String[] segments = line.split(SEPARATOR);
        final String name = segments[0].trim();
        final String version = segments[1].trim();

        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, name, version);
        final Dependency node = new Dependency(name, version, externalId);

        return node;
    }

    private int getCurrentIndentation(String line) {
        int currentIndentation = 0;
        while (line.startsWith(INDENTATION)) {
            currentIndentation++;
            line = line.replaceFirst(INDENTATION, "");
        }

        return currentIndentation;
    }
}
