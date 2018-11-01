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
package com.blackducksoftware.integration.hub.detect.workflow.bdio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.BdioCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.BdioCodeLocationCreator;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.BdioCodeLocationResult;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.FileNameUtils;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.hub.bdio.SimpleBdioFactory;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.hub.bdio.model.Forge;
import com.synopsys.integration.hub.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.hub.bdio.model.SpdxCreator;
import com.synopsys.integration.hub.bdio.model.dependency.Dependency;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class BdioManager {
    private final Logger logger = LoggerFactory.getLogger(BdioManager.class);

    private final DetectInfo detectInfo;
    private final SimpleBdioFactory simpleBdioFactory;
    private final IntegrationEscapeUtil integrationEscapeUtil;
    private final CodeLocationNameManager codeLocationNameManager;
    private final DetectConfiguration detectConfiguration;
    private final BdioCodeLocationCreator bdioCodeLocationCreator;
    private final DirectoryManager directoryManager;

    public BdioManager(final DetectInfo detectInfo, final SimpleBdioFactory simpleBdioFactory, final IntegrationEscapeUtil integrationEscapeUtil, final CodeLocationNameManager codeLocationNameManager,
        final DetectConfiguration detectConfiguration, final BdioCodeLocationCreator codeLocationManager, final DirectoryManager directoryManager) {
        this.detectInfo = detectInfo;
        this.simpleBdioFactory = simpleBdioFactory;
        this.integrationEscapeUtil = integrationEscapeUtil;
        this.codeLocationNameManager = codeLocationNameManager;
        this.detectConfiguration = detectConfiguration;
        this.bdioCodeLocationCreator = codeLocationManager;
        this.directoryManager = directoryManager;
    }

    public BdioResult createBdioFiles(String aggregateName, NameVersion projectNameVersion, List<DetectCodeLocation> codeLocations) throws DetectUserFriendlyException {
        if (StringUtils.isBlank(aggregateName)) {
            final BdioCodeLocationResult codeLocationResult = bdioCodeLocationCreator.createFromDetectCodeLocations(codeLocations, projectNameVersion);
            //codeLocationResult.getFailedBomToolGroupTypes().forEach(it -> bomToolResults.put(it, Result.FAILURE));

            final List<File> createdBdioFiles = createBdioFiles(codeLocationResult.getBdioCodeLocations(), projectNameVersion);

            return new BdioResult(codeLocationResult.getBdioCodeLocations(), createdBdioFiles);
            //reportManager.codeLocationsCompleted(searchResult.getBomToolEvaluations(), codeLocationResult.getCodeLocationNames());
        } else {
            final File aggregateBdioFile = createAggregateBdioFile(codeLocations, projectNameVersion);
            return new BdioResult(new ArrayList<>(), Arrays.asList(aggregateBdioFile));
        }
    }

    private List<File> createBdioFiles(final List<BdioCodeLocation> bdioCodeLocations, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        final List<File> bdioFiles = new ArrayList<>();
        for (final BdioCodeLocation bdioCodeLocation : bdioCodeLocations) {
            final SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(bdioCodeLocation.codeLocationName, projectNameVersion, bdioCodeLocation.codeLocation);

            final File outputFile = new File(directoryManager.getBdioOutputDirectory(), bdioCodeLocation.bdioName);
            if (outputFile.exists()) {
                final boolean deleteSuccess = outputFile.delete();
                logger.debug(String.format("%s deleted: %b", outputFile.getAbsolutePath(), deleteSuccess));
            }
            writeBdioFile(outputFile, simpleBdioDocument);
            bdioFiles.add(outputFile);
        }

        return bdioFiles;
    }

    private File createAggregateBdioFile(final List<DetectCodeLocation> codeLocations, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        final DependencyGraph aggregateDependencyGraph = createAggregateDependencyGraph(codeLocations);

        final SimpleBdioDocument aggregateBdioDocument = createAggregateSimpleBdioDocument(projectNameVersion, aggregateDependencyGraph);
        final String filename = String.format("%s.jsonld", integrationEscapeUtil.escapeForUri(detectConfiguration.getProperty(DetectProperty.DETECT_BOM_AGGREGATE_NAME, PropertyAuthority.None)));
        final File aggregateBdioFile = new File(directoryManager.getBdioOutputDirectory(), filename);
        if (aggregateBdioFile.exists()) {
            final boolean deleteSuccess = aggregateBdioFile.delete();
            logger.debug(String.format("%s deleted: %b", aggregateBdioFile.getAbsolutePath(), deleteSuccess));
        }

        writeBdioFile(aggregateBdioFile, aggregateBdioDocument);

        return aggregateBdioFile;
    }

    private DependencyGraph createAggregateDependencyGraph(final List<DetectCodeLocation> codeLocations) {
        final MutableDependencyGraph aggregateDependencyGraph = simpleBdioFactory.createMutableDependencyGraph();

        for (final DetectCodeLocation detectCodeLocation : codeLocations) {
            final Dependency codeLocationDependency = createAggregateDependency(detectCodeLocation);
            aggregateDependencyGraph.addChildrenToRoot(codeLocationDependency);
            aggregateDependencyGraph.addGraphAsChildrenToParent(codeLocationDependency, detectCodeLocation.getDependencyGraph());
        }

        return aggregateDependencyGraph;
    }

    private Dependency createAggregateDependency(final DetectCodeLocation codeLocation) {
        String name = null;
        String version = null;
        try {
            name = codeLocation.getExternalId().name;
            version = codeLocation.getExternalId().version;
        } catch (final Exception e) {
            logger.warn("Failed to get name or version to use in the wrapper for a code location.", e);
        }
        final ExternalId original = codeLocation.getExternalId();
        final String sourcePath = codeLocation.getSourcePath();
        final String bomToolType = codeLocation.getBomToolGroupType().toString();
        final String relativePath = FileNameUtils.relativize(directoryManager.getSourceDirectory().getAbsolutePath(), sourcePath);
        final List<String> externalIdPieces = new ArrayList<>();
        externalIdPieces.addAll(Arrays.asList(original.getExternalIdPieces()));
        externalIdPieces.add(relativePath);
        externalIdPieces.add(bomToolType);
        final String[] pieces = externalIdPieces.toArray(new String[externalIdPieces.size()]);
        return new Dependency(name, version, new ExternalIdFactory().createModuleNamesExternalId(original.forge, pieces));
    }

    private void writeBdioFile(final File outputFile, final SimpleBdioDocument simpleBdioDocument) throws DetectUserFriendlyException {
        try {
            simpleBdioFactory.writeSimpleBdioDocumentToFile(outputFile, simpleBdioDocument);
            logger.info(String.format("BDIO Generated: %s", outputFile.getAbsolutePath()));
        } catch (final IOException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }

    private SimpleBdioDocument createAggregateSimpleBdioDocument(NameVersion projectNameVersion, final DependencyGraph dependencyGraph) {
        final ExternalId projectExternalId = simpleBdioFactory.createNameVersionExternalId(new Forge("/", "/", ""), projectNameVersion.getName(), projectNameVersion.getVersion());
        final String codeLocationName = codeLocationNameManager.createAggregateCodeLocationName(projectNameVersion);
        return createSimpleBdioDocument(codeLocationName, projectNameVersion, projectExternalId, dependencyGraph);
    }

    private SimpleBdioDocument createSimpleBdioDocument(final String codeLocationName, NameVersion projectNameVersion, final DetectCodeLocation detectCodeLocation) {
        final ExternalId projectExternalId = detectCodeLocation.getExternalId();
        final DependencyGraph dependencyGraph = detectCodeLocation.getDependencyGraph();

        return createSimpleBdioDocument(codeLocationName, projectNameVersion, projectExternalId, dependencyGraph);
    }

    private SimpleBdioDocument createSimpleBdioDocument(final String codeLocationName, NameVersion projectNameVersion, final ExternalId projectExternalId, final DependencyGraph dependencyGraph) {
        final SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument(codeLocationName, projectNameVersion.getName(), projectNameVersion.getVersion(), projectExternalId, dependencyGraph);

        final String hubDetectVersion = detectInfo.getDetectVersion();
        final SpdxCreator hubDetectCreator = SpdxCreator.createToolSpdxCreator("HubDetect", hubDetectVersion);
        simpleBdioDocument.billOfMaterials.creationInfo.setPrimarySpdxCreator(hubDetectCreator);

        return simpleBdioDocument;
    }

}
