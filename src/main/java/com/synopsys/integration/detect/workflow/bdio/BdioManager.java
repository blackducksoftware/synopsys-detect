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

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.bdio2.Bdio2Factory;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadTarget;
import com.synopsys.integration.detect.DetectInfo;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationCreator;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationResult;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.status.DetectorStatus;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class BdioManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectInfo detectInfo;
    private final SimpleBdioFactory simpleBdioFactory;
    private final Bdio2Factory bdio2Factory;
    private final BdioCodeLocationCreator bdioCodeLocationCreator;
    private final DirectoryManager directoryManager;
    private final IntegrationEscapeUtil integrationEscapeUtil;
    private final CodeLocationNameManager codeLocationNameManager;
    private final EventSystem eventSystem;

    public BdioManager(final DetectInfo detectInfo, final SimpleBdioFactory simpleBdioFactory, final Bdio2Factory bdio2Factory, final IntegrationEscapeUtil integrationEscapeUtil, final CodeLocationNameManager codeLocationNameManager,
        final BdioCodeLocationCreator codeLocationManager, final DirectoryManager directoryManager, final EventSystem eventSystem) {
        this.detectInfo = detectInfo;
        this.simpleBdioFactory = simpleBdioFactory;
        this.bdio2Factory = bdio2Factory;
        this.integrationEscapeUtil = integrationEscapeUtil;
        this.codeLocationNameManager = codeLocationNameManager;
        this.bdioCodeLocationCreator = codeLocationManager;
        this.directoryManager = directoryManager;
        this.eventSystem = eventSystem;
    }

    public BdioResult createBdioFiles(final AggregateOptions aggregateOptions, final NameVersion projectNameVersion, final List<DetectCodeLocation> codeLocations, final boolean useBdio2) throws DetectUserFriendlyException {
        final DetectBdioWriter detectBdioWriter = new DetectBdioWriter(simpleBdioFactory, detectInfo);
        final Optional<String> aggregateName = aggregateOptions.getAggregateName();

        if (aggregateOptions.shouldAggregate() && aggregateName.isPresent()) {
            logger.debug("Creating aggregate BDIO file.");
            final AggregateBdioCreator aggregateBdioCreator = new AggregateBdioCreator(bdio2Factory, simpleBdioFactory, integrationEscapeUtil, codeLocationNameManager, detectBdioWriter);
            final Optional<UploadTarget> uploadTarget = createAggregatedBdioUploadTarget(aggregateBdioCreator, codeLocations, projectNameVersion, useBdio2, aggregateName.get(), aggregateOptions.shouldUploadEmptyAggregate());

            return new BdioResult(uploadTarget, useBdio2);
        } else {
            logger.debug("Creating BDIO code locations.");
            final BdioCodeLocationResult codeLocationResult = bdioCodeLocationCreator.createFromDetectCodeLocations(codeLocations, projectNameVersion);
            codeLocationResult.getFailedBomToolGroupTypes().forEach(it -> eventSystem.publishEvent(Event.StatusSummary, new DetectorStatus(it, StatusType.FAILURE)));

            logger.debug("Creating BDIO files from code locations.");
            final CodeLocationBdioCreator codeLocationBdioCreator = new CodeLocationBdioCreator(detectBdioWriter, simpleBdioFactory, bdio2Factory, detectInfo);
            final List<UploadTarget> uploadTargets = createBdioUploadTargets(codeLocationBdioCreator, codeLocationResult.getBdioCodeLocations(), projectNameVersion, useBdio2);

            return new BdioResult(uploadTargets, useBdio2);
        }
    }

    private List<UploadTarget> createBdioUploadTargets(final CodeLocationBdioCreator codeLocationBdioCreator, final List<BdioCodeLocation> bdioCodeLocations, final NameVersion projectNameVersion, final boolean useBdio2)
        throws DetectUserFriendlyException {

        if (useBdio2) {
            return codeLocationBdioCreator.createBdio2Files(directoryManager.getBdioOutputDirectory(), bdioCodeLocations, projectNameVersion);
        }

        return codeLocationBdioCreator.createBdio1Files(directoryManager.getBdioOutputDirectory(), bdioCodeLocations, projectNameVersion);
    }

    private Optional<UploadTarget> createAggregatedBdioUploadTarget(final AggregateBdioCreator aggregateBdioCreator, final List<DetectCodeLocation> detectCodeLocations, final NameVersion projectNameVersion, final boolean useBdio2,
        final String aggregateName, final boolean shouldUploadEmptyAggregate)
        throws DetectUserFriendlyException {

        if (useBdio2) {
            return aggregateBdioCreator.createAggregateBdio2File(aggregateName, shouldUploadEmptyAggregate, directoryManager.getSourceDirectory(), directoryManager.getBdioOutputDirectory(), detectCodeLocations, projectNameVersion);
        }

        return aggregateBdioCreator.createAggregateBdio1File(aggregateName, shouldUploadEmptyAggregate, directoryManager.getSourceDirectory(), directoryManager.getBdioOutputDirectory(), detectCodeLocations, projectNameVersion);
    }
}
