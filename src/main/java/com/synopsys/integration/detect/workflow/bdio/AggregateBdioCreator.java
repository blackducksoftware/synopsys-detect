/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.workflow.bdio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.bdio2.BdioMetadata;
import com.blackducksoftware.bdio2.model.Project;
import com.blackducksoftware.common.value.ProductList;
import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.blackduck.bdio2.Bdio2Document;
import com.synopsys.integration.blackduck.bdio2.Bdio2Factory;
import com.synopsys.integration.blackduck.bdio2.Bdio2Writer;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadTarget;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.FileNameUtils;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class AggregateBdioCreator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Bdio2Factory bdio2Factory;
    private final SimpleBdioFactory simpleBdioFactory;
    private final IntegrationEscapeUtil integrationEscapeUtil;
    private final CodeLocationNameManager codeLocationNameManager;
    private final DetectBdioWriter detectBdioWriter;

    public AggregateBdioCreator(final Bdio2Factory bdio2Factory, final SimpleBdioFactory simpleBdioFactory, final IntegrationEscapeUtil integrationEscapeUtil,
        final CodeLocationNameManager codeLocationNameManager, final DetectBdioWriter detectBdioWriter) {
        this.bdio2Factory = bdio2Factory;
        this.simpleBdioFactory = simpleBdioFactory;
        this.integrationEscapeUtil = integrationEscapeUtil;
        this.codeLocationNameManager = codeLocationNameManager;
        this.detectBdioWriter = detectBdioWriter;
    }

    public Optional<UploadTarget> createAggregateBdio1File(final String aggregateName, final AggregateMode aggregateMode, final boolean uploadEmptyAggregate, final File sourcePath, final File bdioDirectory, final List<DetectCodeLocation> codeLocations,
        final NameVersion projectNameVersion) throws DetectUserFriendlyException {

        final DependencyGraph aggregateDependencyGraph = createAggregateDependencyGraph(sourcePath, codeLocations, aggregateMode);
        final ExternalId projectExternalId = simpleBdioFactory.createNameVersionExternalId(new Forge("/", "DETECT"), projectNameVersion.getName(), projectNameVersion.getVersion());
        final String codeLocationName = codeLocationNameManager.createAggregateCodeLocationName(projectNameVersion);

        final SimpleBdioDocument aggregateBdioDocument = simpleBdioFactory.createSimpleBdioDocument(codeLocationName, projectNameVersion.getName(), projectNameVersion.getVersion(), projectExternalId, aggregateDependencyGraph);

        final String filename = String.format("%s.jsonld", integrationEscapeUtil.escapeForUri(aggregateName));
        final File aggregateBdioFile = new File(bdioDirectory, filename);

        detectBdioWriter.writeBdioFile(aggregateBdioFile, aggregateBdioDocument);

        return createUploadTarget(codeLocationName, aggregateBdioFile, aggregateDependencyGraph, uploadEmptyAggregate);
    }

    public Optional<UploadTarget> createAggregateBdio2File(final String aggregateName, final AggregateMode aggregateMode, final boolean uploadEmptyAggregate, final File sourcePath, final File bdioDirectory, final List<DetectCodeLocation> codeLocations,
        final NameVersion projectNameVersion) throws DetectUserFriendlyException {

        final DependencyGraph aggregateDependencyGraph = createAggregateDependencyGraph(sourcePath, codeLocations, aggregateMode);
        final ExternalId projectExternalId = simpleBdioFactory.createNameVersionExternalId(new Forge("/", "DETECT"), projectNameVersion.getName(), projectNameVersion.getVersion());
        final String codeLocationName = codeLocationNameManager.createAggregateCodeLocationName(projectNameVersion);

        final BdioMetadata bdioMetadata = bdio2Factory.createBdioMetadata(codeLocationName, ZonedDateTime.now(), new ProductList.Builder());
        final Project project = bdio2Factory.createProject(projectExternalId, projectNameVersion.getName(), projectNameVersion.getVersion());
        final Bdio2Document bdio2Document = bdio2Factory.createBdio2Document(bdioMetadata, project, aggregateDependencyGraph);

        final String bdio2Filename = String.format("%s.bdio", integrationEscapeUtil.escapeForUri(aggregateName));
        final File aggregateBdioFile = new File(bdioDirectory, bdio2Filename);

        final Bdio2Writer bdio2Writer = new Bdio2Writer();
        try {
            final OutputStream outputStream = new FileOutputStream(aggregateBdioFile);
            bdio2Writer.writeBdioDocument(outputStream, bdio2Document);
        } catch (final IOException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }

        return createUploadTarget(codeLocationName, aggregateBdioFile, aggregateDependencyGraph, uploadEmptyAggregate);
    }

    private Optional<UploadTarget> createUploadTarget(final String codeLocationName, final File aggregateBdioFile, final DependencyGraph dependencyGraph, final boolean uploadEmptyAggregate) {
        final boolean aggregateHasDependencies = !dependencyGraph.getRootDependencies().isEmpty();
        if (aggregateHasDependencies || uploadEmptyAggregate) {
            return Optional.of(UploadTarget.createDefault(codeLocationName, aggregateBdioFile));
        } else {
            logger.warn("The aggregate contained no dependencies, will not upload aggregate at this time.");
            return Optional.empty();
        }
    }

    private DependencyGraph createAggregateDependencyGraph(final File sourcePath, final List<DetectCodeLocation> codeLocations, final AggregateMode aggregateMode) throws DetectUserFriendlyException {
        final MutableDependencyGraph aggregateDependencyGraph = simpleBdioFactory.createMutableDependencyGraph();

        for (final DetectCodeLocation detectCodeLocation : codeLocations) {
            if (aggregateMode.equals(AggregateMode.DIRECT)) {
                aggregateDependencyGraph.addGraphAsChildrenToRoot(detectCodeLocation.getDependencyGraph());
            } else if (aggregateMode.equals(AggregateMode.TRANSITIVE)) {
                final Dependency codeLocationDependency = createAggregateDependency(sourcePath, detectCodeLocation);
                aggregateDependencyGraph.addChildrenToRoot(codeLocationDependency);
                aggregateDependencyGraph.addGraphAsChildrenToParent(codeLocationDependency, detectCodeLocation.getDependencyGraph());
            } else {
                throw new DetectUserFriendlyException("Did not specify aggregation mode via detect.bom.aggregate.mode, will not aggregate at this time.", ExitCodeType.FAILURE_GENERAL_ERROR);
            }
        }

        return aggregateDependencyGraph;
    }

    private Dependency createAggregateDependency(final File sourcePath, final DetectCodeLocation codeLocation) {
        String name = null;
        String version = null;
        try {
            name = codeLocation.getExternalId().getName();
            version = codeLocation.getExternalId().getVersion();
        } catch (final Exception e) {
            logger.warn("Failed to get name or version to use in the wrapper for a code location.", e);
        }
        final ExternalId original = codeLocation.getExternalId();
        final String codeLocationSourcePath = codeLocation.getSourcePath().toString(); //TODO: what happens when docker is present or no source path or no external id!
        final File codeLocationSourceDir = new File(codeLocationSourcePath);
        final String relativePath = FileNameUtils.relativize(sourcePath.getAbsolutePath(), codeLocationSourceDir.getAbsolutePath());

        final String bomToolType;
        if (codeLocation.getDockerImageName().isPresent()) {
            bomToolType = "docker"; // TODO: Should docker image name be considered here?
        } else {
            bomToolType = codeLocation.getCreatorName().orElse("unknown").toLowerCase();
        }

        final List<String> externalIdPieces = new ArrayList<>();
        externalIdPieces.addAll(Arrays.asList(original.getExternalIdPieces()));
        if (StringUtils.isNotBlank(relativePath)) {
            externalIdPieces.add(relativePath);
        }
        externalIdPieces.add(bomToolType);
        final String[] pieces = externalIdPieces.toArray(new String[externalIdPieces.size()]);
        return new Dependency(name, version, new ExternalIdFactory().createModuleNamesExternalId(original.getForge(), pieces));
    }
}
