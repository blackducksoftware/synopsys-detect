/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.bdio;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.blackduck.bdio2.Bdio2Factory;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadTarget;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationCreator;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationResult;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocationNamesResult;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class BdioManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectInfo detectInfo;
    private final SimpleBdioFactory simpleBdioFactory;
    private final ExternalIdFactory externalIdFactory;
    private final Bdio2Factory bdio2Factory;
    private final BdioCodeLocationCreator bdioCodeLocationCreator;
    private final DirectoryManager directoryManager;
    private final IntegrationEscapeUtil integrationEscapeUtil;
    private final CodeLocationNameManager codeLocationNameManager;

    public BdioManager(final DetectInfo detectInfo, final SimpleBdioFactory simpleBdioFactory, final ExternalIdFactory externalIdFactory, final Bdio2Factory bdio2Factory,
        final IntegrationEscapeUtil integrationEscapeUtil, final CodeLocationNameManager codeLocationNameManager,
        final BdioCodeLocationCreator codeLocationManager, final DirectoryManager directoryManager) {
        this.detectInfo = detectInfo;
        this.simpleBdioFactory = simpleBdioFactory;
        this.externalIdFactory = externalIdFactory;
        this.bdio2Factory = bdio2Factory;
        this.integrationEscapeUtil = integrationEscapeUtil;
        this.codeLocationNameManager = codeLocationNameManager;
        this.bdioCodeLocationCreator = codeLocationManager;
        this.directoryManager = directoryManager;
    }

    public BdioResult createBdioFiles(final BdioOptions bdioOptions, final AggregateOptions aggregateOptions, final NameVersion projectNameVersion, final List<DetectCodeLocation> codeLocations, final boolean useBdio2)
        throws DetectUserFriendlyException {
        final DetectBdioWriter detectBdioWriter = new DetectBdioWriter(simpleBdioFactory, detectInfo);
        final Optional<String> aggregateName = aggregateOptions.getAggregateName();

        List<UploadTarget> uploadTargets = new ArrayList<>();
        Map<DetectCodeLocation, String> codeLocationNamesResult = new HashMap<>();
        if (aggregateOptions.shouldAggregate() && aggregateName.isPresent()) {
            logger.debug("Creating aggregate BDIO file.");

            final AggregateBdioTransformer aggregateBdioTransformer = new AggregateBdioTransformer(simpleBdioFactory);
            final DependencyGraph aggregateDependencyGraph = aggregateBdioTransformer.aggregateCodeLocations(directoryManager.getSourceDirectory(), codeLocations, aggregateOptions.getAggregateMode());
            final boolean aggregateHasDependencies = !aggregateDependencyGraph.getRootDependencies().isEmpty();

            final ExternalId projectExternalId = externalIdFactory.createNameVersionExternalId(new Forge("/", "DETECT"), projectNameVersion.getName(), projectNameVersion.getVersion());
            final String codeLocationName = codeLocationNameManager.createAggregateCodeLocationName(projectNameVersion);

            String ext = useBdio2 ? ".bdio" : ".jsonld";
            final String fileName = integrationEscapeUtil.replaceWithUnderscore(aggregateName.get()) + ext;
            File aggregateBdioFile = new File(directoryManager.getBdioOutputDirectory(), fileName);

            final AggregateBdioWriter aggregateBdioWriter = new AggregateBdioWriter(bdio2Factory, simpleBdioFactory, detectBdioWriter);
            aggregateBdioWriter.writeAggregateBdioFile(aggregateBdioFile, codeLocationName, projectNameVersion, projectExternalId, aggregateDependencyGraph, useBdio2);

            codeLocations.forEach(cl -> codeLocationNamesResult.put(cl, codeLocationName));
            if (aggregateHasDependencies || aggregateOptions.shouldUploadEmptyAggregate()) {
                uploadTargets.add(UploadTarget.createDefault(projectNameVersion, codeLocationName, aggregateBdioFile));
            } else {
                logger.warn("The aggregate contained no dependencies, will not upload aggregate at this time.");
            }
        } else {
            logger.debug("Creating BDIO code locations.");
            final BdioCodeLocationResult codeLocationResult = bdioCodeLocationCreator.createFromDetectCodeLocations(codeLocations, bdioOptions.getProjectCodeLocationPrefix(), bdioOptions.getProjectCodeLocationSuffix(), projectNameVersion);

            logger.debug("Creating BDIO files from code locations.");
            final CodeLocationBdioCreator codeLocationBdioCreator = new CodeLocationBdioCreator(detectBdioWriter, simpleBdioFactory, bdio2Factory, detectInfo);
            final List<UploadTarget> bdioUploadTargets = codeLocationBdioCreator.createBdioFiles(directoryManager.getBdioOutputDirectory(), codeLocationResult.getBdioCodeLocations(), projectNameVersion, useBdio2);
            uploadTargets.addAll(bdioUploadTargets);
            codeLocationNamesResult.putAll(codeLocationResult.getCodeLocationNames());
        }
        return new BdioResult(uploadTargets, new DetectCodeLocationNamesResult(codeLocationNamesResult), useBdio2);
    }
}
