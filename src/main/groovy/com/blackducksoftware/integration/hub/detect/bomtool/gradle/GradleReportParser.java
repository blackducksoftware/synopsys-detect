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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Stack;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.model.DetectProject;

@Component
public class GradleReportParser {
    private final Logger logger = LoggerFactory.getLogger(GradleReportParser.class);

    private String rootProjectName = "";
    private String rootProjectVersionName = "";
    private String projectSourcePath = "";
    private String projectGroup = "";
    private String projectName = "";
    private String projectVersionName = "";
    private boolean processingMetaData = false;
    private MutableDependencyGraph graph = new MutableMapDependencyGraph();
    private Stack<Dependency> nodeStack = new Stack<>();
    private Dependency previousNode = null;
    private int previousTreeLevel = 0;
    private GradleReportConfigurationParser gradleReportConfigurationParser;

    @Autowired
    private ExternalIdFactory externalIdFactory;

    public DetectCodeLocation parseDependencies(final DetectProject detectProject, final InputStream dependenciesInputStream) {
        clearState();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(dependenciesInputStream, StandardCharsets.UTF_8));
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
                    processMetaDataLine(line);
                    continue;
                }

                if (StringUtils.isBlank(line)) {
                    clearConfigurationState();
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
        } catch (final Exception e) {
            logger.error("Exception parsing gradle output: " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(reader);
        }
        detectProject.setProjectNameIfNotSet(rootProjectName);
        detectProject.setProjectVersionNameIfNotSet(rootProjectVersionName);

        final ExternalId id = externalIdFactory.createMavenExternalId(projectGroup, projectName, projectVersionName);
        final DetectCodeLocation detectCodeLocation = new DetectCodeLocation(BomToolType.GRADLE, projectSourcePath, projectName, projectVersionName, id, graph);
        return detectCodeLocation;
    }

    private void clearState() {
        rootProjectName = "";
        rootProjectVersionName = "";
        projectSourcePath = "";
        projectGroup = "";
        projectName = "";
        projectVersionName = "";
        processingMetaData = false;
        graph = new MutableMapDependencyGraph();
        nodeStack = new Stack<>();
        previousNode = null;
        clearConfigurationState();
    }

    private void clearConfigurationState() {
        nodeStack = new Stack<>();
        previousNode = null;
        previousTreeLevel = 0;
        gradleReportConfigurationParser = new GradleReportConfigurationParser();
    }

    private void processMetaDataLine(final String metaDataLine) {
        if (metaDataLine.startsWith("rootProjectName:")) {
            rootProjectName = metaDataLine.substring("rootProjectName:".length()).trim();
        } else if (metaDataLine.startsWith("rootProjectVersion:")) {
            rootProjectVersionName = metaDataLine.substring("rootProjectVersion:".length()).trim();
        } else if (metaDataLine.startsWith("projectPath:")) {
            projectSourcePath = metaDataLine.substring("projectPath:".length()).trim();
        } else if (metaDataLine.startsWith("projectGroup:")) {
            projectGroup = metaDataLine.substring("projectGroup:".length()).trim();
        } else if (metaDataLine.startsWith("projectName:")) {
            projectName = metaDataLine.substring("projectName:".length()).trim();
        } else if (metaDataLine.startsWith("projectVersion:")) {
            projectVersionName = metaDataLine.substring("projectVersion:".length()).trim();
        }
    }

}
