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
package com.blackducksoftware.integration.hub.detect.workflow.bdio;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.FileNameUtils;
import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.SimpleBdioDocument;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadTarget;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class AggregateBdioCreator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SimpleBdioFactory simpleBdioFactory;
    private final IntegrationEscapeUtil integrationEscapeUtil;
    private final CodeLocationNameManager codeLocationNameManager;
    private final DetectConfiguration detectConfiguration;
    private final DetectBdioWriter detectBdioWriter;

    public AggregateBdioCreator(final SimpleBdioFactory simpleBdioFactory, final IntegrationEscapeUtil integrationEscapeUtil,
            final CodeLocationNameManager codeLocationNameManager, final DetectConfiguration detectConfiguration, DetectBdioWriter detectBdioWriter) {
        this.simpleBdioFactory = simpleBdioFactory;
        this.integrationEscapeUtil = integrationEscapeUtil;
        this.codeLocationNameManager = codeLocationNameManager;
        this.detectConfiguration = detectConfiguration;
        this.detectBdioWriter = detectBdioWriter;
    }

    public Optional<UploadTarget> createAggregateBdioFile(File sourcePath, File bdioDirectory, final List<DetectCodeLocation> codeLocations, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        final DependencyGraph aggregateDependencyGraph = createAggregateDependencyGraph(sourcePath, codeLocations);
        if (aggregateDependencyGraph.getRootDependencies().size() == 0) {
            logger.info("The aggregate contained no dependencies, will not create bdio file.");
            return Optional.empty();
        }

        final ExternalId projectExternalId = simpleBdioFactory.createNameVersionExternalId(new Forge("/", "/", ""), projectNameVersion.getName(), projectNameVersion.getVersion());
        final String codeLocationName = codeLocationNameManager.createAggregateCodeLocationName(projectNameVersion);
        final SimpleBdioDocument aggregateBdioDocument = simpleBdioFactory.createSimpleBdioDocument(codeLocationName, projectNameVersion.getName(), projectNameVersion.getVersion(), projectExternalId, aggregateDependencyGraph);

        final String filename = String.format("%s.jsonld", integrationEscapeUtil.escapeForUri(detectConfiguration.getProperty(DetectProperty.DETECT_BOM_AGGREGATE_NAME, PropertyAuthority.None)));
        final File aggregateBdioFile = new File(bdioDirectory, filename);

        detectBdioWriter.writeBdioFile(aggregateBdioFile, aggregateBdioDocument);

        return Optional.of(UploadTarget.createDefault(codeLocationName, aggregateBdioFile));
    }

    private DependencyGraph createAggregateDependencyGraph(File sourcePath, final List<DetectCodeLocation> codeLocations) {
        final MutableDependencyGraph aggregateDependencyGraph = simpleBdioFactory.createMutableDependencyGraph();

        for (final DetectCodeLocation detectCodeLocation : codeLocations) {
            final Dependency codeLocationDependency = createAggregateDependency(sourcePath, detectCodeLocation);
            aggregateDependencyGraph.addChildrenToRoot(codeLocationDependency);
            aggregateDependencyGraph.addGraphAsChildrenToParent(codeLocationDependency, detectCodeLocation.getDependencyGraph());
        }

        return aggregateDependencyGraph;
    }

    private Dependency createAggregateDependency(File sourcePath, final DetectCodeLocation codeLocation) {
        String name = null;
        String version = null;
        try {
            name = codeLocation.getExternalId().name;
            version = codeLocation.getExternalId().version;
        } catch (final Exception e) {
            logger.warn("Failed to get name or version to use in the wrapper for a code location.", e);
        }
        final ExternalId original = codeLocation.getExternalId();
        final String codeLocationSourcePath = codeLocation.getSourcePath();
        final String bomToolType = codeLocation.getCodeLocationType().toString();
        final String relativePath = FileNameUtils.relativize(sourcePath.getAbsolutePath(), codeLocationSourcePath);
        final List<String> externalIdPieces = new ArrayList<>();
        externalIdPieces.addAll(Arrays.asList(original.getExternalIdPieces()));
        externalIdPieces.add(relativePath);
        externalIdPieces.add(bomToolType);
        final String[] pieces = externalIdPieces.toArray(new String[externalIdPieces.size()]);
        return new Dependency(name, version, new ExternalIdFactory().createModuleNamesExternalId(original.forge, pieces));
    }
}
