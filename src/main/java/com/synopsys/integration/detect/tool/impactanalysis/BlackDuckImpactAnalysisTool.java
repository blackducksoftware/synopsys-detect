/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.tool.impactanalysis;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysis;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchOutput;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisUploadService;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;
import com.synopsys.method.analyzer.core.MethodUseAnalyzer;

public class BlackDuckImpactAnalysisTool {
    public static final String STATUS_KEY = "IMPACT_ANALYSIS";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DirectoryManager directoryManager;
    private final CodeLocationNameManager codeLocationNameManager;
    private final ImpactAnalysisOptions impactAnalysisOptions;
    @Nullable
    private final BlackDuckServicesFactory blackDuckServicesFactory;
    private final EventSystem eventSystem;

    public BlackDuckImpactAnalysisTool(DirectoryManager directoryManager, CodeLocationNameManager codeLocationNameManager, ImpactAnalysisOptions impactAnalysisOptions, @Nullable BlackDuckServicesFactory blackDuckServicesFactory,
        EventSystem eventSystem) {
        this.directoryManager = directoryManager;
        this.codeLocationNameManager = codeLocationNameManager;
        this.impactAnalysisOptions = impactAnalysisOptions;
        this.blackDuckServicesFactory = blackDuckServicesFactory;
        this.eventSystem = eventSystem;
    }

    public boolean shouldRun() {
        return Boolean.TRUE.equals(impactAnalysisOptions.isEnabled());
    }

    public ImpactAnalysisToolResult performImpactAnalysisActions(NameVersion projectNameAndVersion) throws DetectUserFriendlyException {
        String codeLocationName = codeLocationNameManager.createImpactAnalysisCodeLocationName(directoryManager.getSourceDirectory(), projectNameAndVersion.getName(), projectNameAndVersion.getVersion(), null, null);
        Path impactAnalysisPath;
        try {
            impactAnalysisPath = generateImpactAnalysis(codeLocationName);
        } catch (IOException e) {
            return failImpactAnalysis(e.getMessage());
        }

        if (blackDuckServicesFactory != null) {
            if (impactAnalysisPath != null && impactAnalysisPath.toFile().isFile() && impactAnalysisPath.toFile().canRead()) {
                CodeLocationCreationData<ImpactAnalysisBatchOutput> codeLocationCreationData = uploadImpactAnalysis(impactAnalysisPath, projectNameAndVersion, codeLocationName, ImpactAnalysisUploadService.create(blackDuckServicesFactory));
                // TODO: Manually map code locations.
                
                return ImpactAnalysisToolResult.SUCCESS(codeLocationCreationData, impactAnalysisPath);
            } else {
                return failImpactAnalysis("Impact analysis file did not exist, is not a file or can't be read.");
            }
        } else {
            logger.debug("Skipping report upload.");
            return ImpactAnalysisToolResult.SUCCESS(impactAnalysisPath);
        }

    }

    private ImpactAnalysisToolResult failImpactAnalysis(String issueMessage) {
        logger.warn(issueMessage);
        eventSystem.publishEvent(Event.StatusSummary, new Status(STATUS_KEY, StatusType.FAILURE));
        eventSystem.publishEvent(Event.Issue, new DetectIssue(DetectIssueType.BINARY_SCAN, Collections.singletonList(issueMessage)));
        eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR, STATUS_KEY));
        return ImpactAnalysisToolResult.FAILURE();
    }

    public Path generateImpactAnalysis(String impactAnalysisCodeLocationName) throws IOException {
        MethodUseAnalyzer analyzer = new MethodUseAnalyzer();
        Path outputReportPath = analyzer.analyze(directoryManager.getSourceDirectory().toPath(), directoryManager.getImpactAnalysisOutputDirectory().toPath(), impactAnalysisCodeLocationName);
        logger.info(String.format("Vulnerability Impact Analysis generated report at %s", outputReportPath));
        return outputReportPath;
    }

    public CodeLocationCreationData<ImpactAnalysisBatchOutput> uploadImpactAnalysis(Path impactAnalysisPath, NameVersion projectNameVersion, String codeLocationName, ImpactAnalysisUploadService impactAnalysisService)
        throws DetectUserFriendlyException {
        ImpactAnalysis impactAnalysis = new ImpactAnalysis(impactAnalysisPath, projectNameVersion.getName(), projectNameVersion.getVersion(), codeLocationName);
        try {
            logger.info(String.format("Preparing to impact analysis file: %s", codeLocationName));
            CodeLocationCreationData<ImpactAnalysisBatchOutput> codeLocationCreationData = impactAnalysisService.uploadImpactAnalysis(impactAnalysis);

            ImpactAnalysisBatchOutput impactAnalysisBatchOutput = codeLocationCreationData.getOutput();
            impactAnalysisBatchOutput.throwExceptionForError(logger);

            logger.info(String.format("Successfully uploaded impact analysis file: %s", codeLocationName));
            eventSystem.publishEvent(Event.StatusSummary, new Status(STATUS_KEY, StatusType.SUCCESS));
            return codeLocationCreationData;
        } catch (IntegrationException exception) {
            logger.error(String.format("Failed to upload impact analysis file: %s", exception.getMessage()));
            eventSystem.publishEvent(Event.StatusSummary, new Status(STATUS_KEY, StatusType.FAILURE));
            eventSystem.publishEvent(Event.Issue, new DetectIssue(DetectIssueType.EXCEPTION, Collections.singletonList(exception.getMessage())));
            throw new DetectUserFriendlyException("Failed to upload impact analysis file.", exception, ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
        }
    }
}
