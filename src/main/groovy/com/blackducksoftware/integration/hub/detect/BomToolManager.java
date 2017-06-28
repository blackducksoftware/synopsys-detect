/**
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.simple.BdioWriter;
import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeTransformer;
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.output.DetectProject;
import com.blackducksoftware.integration.hub.detect.type.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer;
import com.blackducksoftware.integration.util.ExcludedIncludedFilter;
import com.blackducksoftware.integration.util.IntegrationEscapeUtil;
import com.google.gson.Gson;

@Component
public class BomToolManager {
    private final Logger logger = LoggerFactory.getLogger(BomToolManager.class);

    @Autowired
    DetectConfiguration detectConfiguration;

    @Autowired
    ProjectInfoGatherer projectInfoGatherer;

    @Autowired
    private List<BomTool> bomTools;

    @Autowired
    private Gson gson;

    @Autowired
    private DependencyNodeTransformer dependencyNodeTransformer;

    private final Map<String, String> filenameToProjectName = new HashMap<>();

    private final Map<String, String> filenameToProjectVersionName = new HashMap<>();

    public List<File> createBdioFiles() throws IOException {
        final List<File> createdBdioFiles = new ArrayList<>();
        boolean foundSomeBomTools = false;
        final ExcludedIncludedFilter toolFilter = new ExcludedIncludedFilter(detectConfiguration.getExcludedBomToolTypes(),
                detectConfiguration.getIncludedBomToolTypes());
        for (final BomTool bomTool : bomTools) {
            try {
                final BomToolType bomToolType = bomTool.getBomToolType();
                final String bomToolTypeString = bomToolType.toString();
                if (!toolFilter.shouldInclude(bomToolTypeString)) {
                    logger.debug(String.format("Skipping %s.", bomToolTypeString));
                    continue;
                }
                if (bomTool.isBomToolApplicable() && detectConfiguration.shouldRun(bomTool)) {
                    logger.info(bomToolType + " applies given the current configuration.");
                    final List<DetectProject> projects = bomTool.extractDetectProjects();
                    if (projects != null && projects.size() > 0) {
                        foundSomeBomTools = true;
                        createOutput(createdBdioFiles, bomToolType, projects);
                    } else {
                        logger.error("Did not find any projects from " + bomToolType);
                    }
                }
            } catch (final Exception e) {
                // any bom tool failure should not prevent other bom tools from running
                logger.error(bomTool.getBomToolType().toString() + " threw an Exception: " + e.getMessage());
                if (logger.isTraceEnabled()) {
                    e.printStackTrace();
                }
            }
        }

        if (!foundSomeBomTools) {
            logger.debug("Could not find any tools to run.");
        }
        return createdBdioFiles;
    }

    private void createOutput(final List<File> createdBdioFiles, final BomToolType bomToolType, final List<DetectProject> projects) {
        logger.info("Found " + projects.size() + " projects");
        for (final DetectProject project : projects) {
            logger.info("Creating " + project.getDependencyNodes().size() + " project nodes");
            for (final DependencyNode dependencyNode : project.getDependencyNodes()) {
                final IntegrationEscapeUtil escapeUtil = new IntegrationEscapeUtil();
                final String safeProjectName = escapeUtil.escapeForUri(dependencyNode.name);
                final String safeVersionName = escapeUtil.escapeForUri(dependencyNode.version);
                final String safeName = String.format("%s_%s_%s_%s_bdio", bomToolType.toString(), project.getTargetName(), safeProjectName, safeVersionName);
                final String filename = String.format("%s.jsonld", safeName);
                final File outputFile = new File(detectConfiguration.getOutputDirectory(), filename);
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                // TODO Use project path to generate a unique name in hub and stop returning a bdioDocument.
                try (final BdioWriter bdioWriter = new BdioWriter(gson, new FileOutputStream(outputFile))) {
                    final String codeLocation = projectInfoGatherer.getCodeLocationName(bomToolType, project.getTargetName(), dependencyNode.name,
                            dependencyNode.version);
                    final SimpleBdioDocument bdioDocument = dependencyNodeTransformer.transformDependencyNode(codeLocation, dependencyNode);

                    bdioWriter.writeSimpleBdioDocument(bdioDocument);
                    createdBdioFiles.add(outputFile);
                    logger.info("BDIO Generated: " + outputFile.getAbsolutePath());
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }

                filenameToProjectName.put(filename, dependencyNode.name);
                filenameToProjectVersionName.put(filename, dependencyNode.version);
            }
        }
    }

    public String getProjectNameByBdioFilename(final String bdioFilename) {
        return filenameToProjectName.get(bdioFilename);
    }

    public String getProjectVersionNameByBdioFilename(final String bdioFilename) {
        return filenameToProjectVersionName.get(bdioFilename);
    }

}
