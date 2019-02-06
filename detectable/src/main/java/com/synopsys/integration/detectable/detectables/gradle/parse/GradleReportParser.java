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
package com.synopsys.integration.detectable.detectables.gradle.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocationType;
import com.synopsys.integration.detectable.detectable.util.DependencyHistory;
import com.synopsys.integration.util.NameVersion;

public class GradleReportParser {
    private final Logger logger = LoggerFactory.getLogger(GradleReportParser.class);

    public static final String PROJECT_PATH_PREFIX = "projectPath:";
    public static final String PROJECT_GROUP_PREFIX = "projectGroup:";
    public static final String PROJECT_NAME_PREFIX = "projectName:";
    public static final String PROJECT_VERSION_PREFIX = "projectVersion:";
    public static final String ROOT_PROJECT_NAME_PREFIX = "rootProjectName:";
    public static final String ROOT_PROJECT_VERSION_PREFIX = "rootProjectVersion:";
    public static final String DETECT_META_DATA_HEADER = "DETECT META DATA START";
    public static final String DETECT_META_DATA_FOOTER = "DETECT META DATA END";

    private final ExternalIdFactory externalIdFactory;

    private GradleReportConfigurationParser gradleReportConfigurationParser = new GradleReportConfigurationParser();

    public GradleReportParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public Optional<CodeLocation> parseDependencies(final File codeLocationFile) {
        CodeLocation codeLocation = null;
        String projectSourcePath = "";
        String projectGroup = "";
        String projectName = "";
        String projectVersionName = "";
        boolean processingMetaData = false;
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        final DependencyHistory history = new DependencyHistory();

        try (FileInputStream dependenciesInputStream = new FileInputStream(codeLocationFile); BufferedReader reader = new BufferedReader(new InputStreamReader(dependenciesInputStream, StandardCharsets.UTF_8));) {
            while (reader.ready()) {
                final String line = reader.readLine();
                /**
                 * The meta data section will be at the end of the file after all of the "gradle dependencies" output
                 */
                if (line.startsWith(DETECT_META_DATA_HEADER)) {
                    processingMetaData = true;
                    continue;
                }
                if (line.startsWith(DETECT_META_DATA_FOOTER)) {
                    processingMetaData = false;
                    continue;
                }
                if (processingMetaData) {
                    if (line.startsWith(PROJECT_PATH_PREFIX)) {
                        projectSourcePath = line.substring(PROJECT_PATH_PREFIX.length()).trim();
                    } else if (line.startsWith(PROJECT_GROUP_PREFIX)) {
                        projectGroup = line.substring(PROJECT_GROUP_PREFIX.length()).trim();
                    } else if (line.startsWith(PROJECT_NAME_PREFIX)) {
                        projectName = line.substring(PROJECT_NAME_PREFIX.length()).trim();
                    } else if (line.startsWith(PROJECT_VERSION_PREFIX)) {
                        projectVersionName = line.substring(PROJECT_VERSION_PREFIX.length()).trim();
                    }
                    continue;
                }

                if (StringUtils.isBlank(line)) {
                    history.clear();
                    gradleReportConfigurationParser = new GradleReportConfigurationParser();
                    continue;
                }

                final Dependency dependency = gradleReportConfigurationParser.parseDependency(externalIdFactory, line);
                if (dependency == null) {
                    continue;
                }

                final int lineTreeLevel = gradleReportConfigurationParser.getTreeLevel();

                try {
                    history.clearDependenciesDeeperThan(lineTreeLevel);
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

            final ExternalId id = externalIdFactory.createMavenExternalId(projectGroup, projectName, projectVersionName);
            codeLocation = new CodeLocation.Builder(CodeLocationType.GRADLE, graph, id).build(); //TODO: Source Path?
        } catch (final IOException e) {
            codeLocation = null;
        }

        return Optional.ofNullable(codeLocation);
    }

    public Optional<NameVersion> parseRootProjectNameVersion(final File rootProjectMetadataFile) {
        NameVersion nameVersion = null;
        String rootProjectName = null;
        String rootProjectVersionName = null;
        boolean processingMetaData = false;

        try (FileInputStream dependenciesInputStream = new FileInputStream(rootProjectMetadataFile); BufferedReader reader = new BufferedReader(new InputStreamReader(dependenciesInputStream, StandardCharsets.UTF_8));) {
            while (reader.ready()) {
                final String line = reader.readLine();

                if (line.startsWith(DETECT_META_DATA_HEADER)) {
                    processingMetaData = true;
                    continue;
                }
                if (line.startsWith(DETECT_META_DATA_FOOTER)) {
                    processingMetaData = false;
                    continue;
                }
                if (processingMetaData) {
                    if (line.startsWith(ROOT_PROJECT_NAME_PREFIX)) {
                        rootProjectName = line.substring(ROOT_PROJECT_NAME_PREFIX.length()).trim();
                    } else if (line.startsWith(ROOT_PROJECT_VERSION_PREFIX)) {
                        rootProjectVersionName = line.substring(ROOT_PROJECT_VERSION_PREFIX.length()).trim();
                    }
                    continue;
                }
            }
            nameVersion = new NameVersion(rootProjectName, rootProjectVersionName);
        } catch (final IOException e) {
            nameVersion = null;
        }

        return Optional.ofNullable(nameVersion);
    }

}
