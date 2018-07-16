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
package com.blackducksoftware.integration.hub.detect.bomtool.gradle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.util.NameVersion;

public class GradleReportParser {
    private final Logger logger = LoggerFactory.getLogger(GradleReportParser.class);
    private GradleReportConfigurationParser gradleReportConfigurationParser = new GradleReportConfigurationParser();

    private final ExternalIdFactory externalIdFactory;

    public GradleReportParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public Optional<DetectCodeLocation> parseDependencies(final BomToolType bomToolType, final File codeLocationFile) {
        DetectCodeLocation codeLocation = null;
        String projectSourcePath = "";
        String projectGroup = "";
        String projectName = "";
        String projectVersionName = "";
        boolean processingMetaData = false;
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        Stack<Dependency> nodeStack = new Stack<>();
        Dependency previousNode = null;
        int previousTreeLevel = 0;

        try (FileInputStream dependenciesInputStream = new FileInputStream(codeLocationFile); BufferedReader reader = new BufferedReader(new InputStreamReader(dependenciesInputStream, StandardCharsets.UTF_8));) {
            while (reader.ready()) {
                final String line = reader.readLine();
                /**
                 * The meta data section will be at the end of the file after all of the "gradle dependencies" output
                 */
                if (line.startsWith("DETECT META DATA START")) {
                    processingMetaData = true;
                    continue;
                }
                if (line.startsWith("DETECT META DATA END")) {
                    processingMetaData = false;
                    continue;
                }
                if (processingMetaData) {
                    if (line.startsWith("projectPath:")) {
                        projectSourcePath = line.substring("projectPath:".length()).trim();
                    } else if (line.startsWith("projectGroup:")) {
                        projectGroup = line.substring("projectGroup:".length()).trim();
                    } else if (line.startsWith("projectName:")) {
                        projectName = line.substring("projectName:".length()).trim();
                    } else if (line.startsWith("projectVersion:")) {
                        projectVersionName = line.substring("projectVersion:".length()).trim();
                    }
                    continue;
                }

                if (StringUtils.isBlank(line)) {
                    nodeStack = new Stack<>();
                    previousNode = null;
                    previousTreeLevel = 0;
                    gradleReportConfigurationParser = new GradleReportConfigurationParser();
                    continue;
                }

                final Dependency nextDependency = gradleReportConfigurationParser.parseDependency(externalIdFactory, line);
                if (nextDependency == null) {
                    continue;
                }

                final int lineTreeLevel = gradleReportConfigurationParser.getTreeLevel();
                if (lineTreeLevel == previousTreeLevel + 1) {
                    nodeStack.push(previousNode);
                } else if (lineTreeLevel < previousTreeLevel) {
                    for (int times = 0; times < (previousTreeLevel - lineTreeLevel); times++) {
                        nodeStack.pop();
                    }
                } else if (lineTreeLevel != previousTreeLevel) {
                    logger.error(String.format("The tree level (%s) and this line (%s) with count %s can't be reconciled.", previousTreeLevel, line, lineTreeLevel));
                }
                if (nodeStack.size() == 0) {
                    graph.addChildToRoot(nextDependency);
                } else {
                    graph.addChildWithParents(nextDependency, nodeStack.peek());
                }
                previousNode = nextDependency;
                previousTreeLevel = lineTreeLevel;
            }

            final ExternalId id = externalIdFactory.createMavenExternalId(projectGroup, projectName, projectVersionName);
            codeLocation = new DetectCodeLocation.Builder(BomToolGroupType.GRADLE, bomToolType, projectSourcePath, id, graph).build();
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

                if (line.startsWith("DETECT META DATA START")) {
                    processingMetaData = true;
                    continue;
                }
                if (line.startsWith("DETECT META DATA END")) {
                    processingMetaData = false;
                    continue;
                }
                if (processingMetaData) {
                    if (line.startsWith("rootProjectName:")) {
                        rootProjectName = line.substring("rootProjectName:".length()).trim();
                    } else if (line.startsWith("rootProjectVersion:")) {
                        rootProjectVersionName = line.substring("rootProjectVersion:".length()).trim();
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
