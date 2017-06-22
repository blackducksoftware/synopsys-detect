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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.simple.BdioWriter;
import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeTransformer;
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.type.BomToolType;
import com.blackducksoftware.integration.util.ExcludedIncludedFilter;
import com.blackducksoftware.integration.util.IntegrationEscapeUtil;
import com.google.gson.Gson;

@Component
public class BomToolManager {
    private final Logger logger = LoggerFactory.getLogger(BomToolManager.class);

    @Autowired
    DetectConfiguration detectConfiguration;

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
        final ExcludedIncludedFilter toolFilter = new ExcludedIncludedFilter("", detectConfiguration.getBomToolTypeOverride());
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
                    final List<DependencyNode> projectNodes = bomTool.extractDependencyNodes();
                    if (projectNodes != null && projectNodes.size() > 0) {
                        foundSomeBomTools = true;
                        createOutput(createdBdioFiles, bomToolType, bomToolTypeString, projectNodes);
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

    private void createOutput(final List<File> createdBdioFiles, final BomToolType bomToolType, final String bomToolTypeString,
            final List<DependencyNode> projectNodes) {
        logger.info("Creating " + projectNodes.size() + " project nodes");
        for (final DependencyNode project : projectNodes) {
            final IntegrationEscapeUtil escapeUtil = new IntegrationEscapeUtil();
            final String safeProjectName = escapeUtil.escapeForUri(project.name);
            final String safeVersionName = escapeUtil.escapeForUri(project.version);
            final String safeName = String.format("%s_%s_%s_bdio", bomToolTypeString, safeProjectName, safeVersionName);
            final String filename = String.format("%s.jsonld", safeName);
            final File outputFile = new File(detectConfiguration.getOutputDirectory(), filename);
            if (outputFile.exists()) {
                outputFile.delete();
            }
            try (final BdioWriter bdioWriter = new BdioWriter(gson, new FileOutputStream(outputFile))) {
                if (StringUtils.isNotBlank(detectConfiguration.getProjectName())) {
                    project.name = detectConfiguration.getProjectName();
                }
                if (StringUtils.isNotBlank(detectConfiguration.getProjectVersionName())) {
                    project.version = detectConfiguration.getProjectVersionName();
                }
                final SimpleBdioDocument bdioDocument = dependencyNodeTransformer.transformDependencyNode(detectConfiguration.getProjectCodeLocation(),
                        project);
                if (StringUtils.isNotBlank(detectConfiguration.getProjectName()) && StringUtils.isNotBlank(detectConfiguration.getProjectVersionName())) {
                    bdioDocument.billOfMaterials.spdxName = String.format("%s/%s/%s Black Duck I/O Export", project.name, project.version, bomToolTypeString);
                }
                bdioWriter.writeSimpleBdioDocument(bdioDocument);
                createdBdioFiles.add(outputFile);
                logger.info("BDIO Generated: " + outputFile.getAbsolutePath());
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }

            filenameToProjectName.put(filename, project.name);
            filenameToProjectVersionName.put(filename, project.version);
        }
    }

    public String getProjectNameByBdioFilename(final String bdioFilename) {
        return filenameToProjectName.get(bdioFilename);
    }

    public String getProjectVersionNameByBdioFilename(final String bdioFilename) {
        return filenameToProjectVersionName.get(bdioFilename);
    }
}
