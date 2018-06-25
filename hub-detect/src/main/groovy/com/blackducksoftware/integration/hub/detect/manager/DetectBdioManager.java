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
package com.blackducksoftware.integration.hub.detect.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.SimpleBdioFactory;
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.bdio.model.ToolSpdxCreator;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.model.BdioCodeLocation;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.util.IntegrationEscapeUtil;

@Component
public class DetectBdioManager {
    private final Logger logger = LoggerFactory.getLogger(DetectBdioManager.class);

    @Autowired
    private DetectInfo detectInfo;

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private SimpleBdioFactory simpleBdioFactory;

    @Autowired
    private IntegrationEscapeUtil integrationEscapeUtil;

    @Autowired
    private CodeLocationNameManager codeLocationNameManager;

    public List<File> createBdioFiles(final List<BdioCodeLocation> bdioCodeLocations, final String projectName, final String projectVersion) throws DetectUserFriendlyException {
        final List<File> bdioFiles = new ArrayList<>();
        for (final BdioCodeLocation bdioCodeLocation : bdioCodeLocations) {
            final SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(bdioCodeLocation.codeLocationName, projectName, projectVersion, bdioCodeLocation.codeLocation);

            final File outputFile = new File(detectConfiguration.getBdioOutputDirectoryPath(), bdioCodeLocation.bdioName);
            if (outputFile.exists()) {
                final boolean deleteSuccess = outputFile.delete();
                logger.debug(String.format("%s deleted: %b", outputFile.getAbsolutePath(), deleteSuccess));
            }
            writeBdioFile(outputFile, simpleBdioDocument);
            bdioFiles.add(outputFile);
        }

        return bdioFiles;
    }

    public File createAggregateBdioFile(final List<DetectCodeLocation> codeLocations, final String projectName, final String projectVersion) throws DetectUserFriendlyException {
        final MutableDependencyGraph aggregateDependencyGraph = simpleBdioFactory.createMutableDependencyGraph();

        for (final DetectCodeLocation detectCodeLocation : codeLocations) {
            aggregateDependencyGraph.addGraphAsChildrenToRoot(detectCodeLocation.getDependencyGraph());
        }
        final SimpleBdioDocument aggregateBdioDocument = createAggregateSimpleBdioDocument(projectName, projectVersion, aggregateDependencyGraph);
        final String filename = String.format("%s.jsonld", integrationEscapeUtil.escapeForUri(detectConfiguration.getAggregateBomName()));
        final File aggregateBdioFile = new File(detectConfiguration.getOutputDirectory(), filename);
        if (aggregateBdioFile.exists()) {
            final boolean deleteSuccess = aggregateBdioFile.delete();
            logger.debug(String.format("%s deleted: %b", aggregateBdioFile.getAbsolutePath(), deleteSuccess));
        }

        writeBdioFile(aggregateBdioFile, aggregateBdioDocument);

        return aggregateBdioFile;
    }

    private void writeBdioFile(final File outputFile, final SimpleBdioDocument simpleBdioDocument) throws DetectUserFriendlyException {
        try {
            simpleBdioFactory.writeSimpleBdioDocumentToFile(outputFile, simpleBdioDocument);
            logger.info(String.format("BDIO Generated: %s", outputFile.getAbsolutePath()));
        } catch (final IOException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }

    private SimpleBdioDocument createAggregateSimpleBdioDocument(final String projectName, final String projectVersionName, final DependencyGraph dependencyGraph) {
        final ExternalId projectExternalId = simpleBdioFactory.createNameVersionExternalId(new Forge("/", "/", ""), projectName, projectVersionName);
        final String codeLocationName = codeLocationNameManager.createAggregateCodeLocationName();
        return createSimpleBdioDocument(codeLocationName, projectName, projectVersionName, projectExternalId, dependencyGraph);
    }

    private SimpleBdioDocument createSimpleBdioDocument(final String codeLocationName, final String projectName, final String projectVersionName, final DetectCodeLocation detectCodeLocation) {
        final ExternalId projectExternalId = detectCodeLocation.getExternalId();
        final DependencyGraph dependencyGraph = detectCodeLocation.getDependencyGraph();

        return createSimpleBdioDocument(codeLocationName, projectName, projectVersionName, projectExternalId, dependencyGraph);
    }

    private SimpleBdioDocument createSimpleBdioDocument(final String codeLocationName, final String projectName, final String projectVersionName, final ExternalId projectExternalId, final DependencyGraph dependencyGraph) {
        final SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument(codeLocationName, projectName, projectVersionName, projectExternalId, dependencyGraph);

        final String hubDetectVersion = detectInfo.getDetectVersion();
        final ToolSpdxCreator hubDetectCreator = new ToolSpdxCreator("HubDetect", hubDetectVersion);
        simpleBdioDocument.billOfMaterials.creationInfo.addSpdxCreator(hubDetectCreator);

        return simpleBdioDocument;
    }

}
