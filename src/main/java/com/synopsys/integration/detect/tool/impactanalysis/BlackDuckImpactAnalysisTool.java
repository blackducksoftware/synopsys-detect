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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.view.CodeLocationView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.CodeLocationService;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysis;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchOutput;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisOutput;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisUploadService;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisUploadView;
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
    private final EventSystem eventSystem;
    private final ImpactAnalysisUploadService impactAnalysisUploadService;
    private final BlackDuckService blackDuckService;
    private final CodeLocationService codeLocationService;
    private final boolean online;

    public static BlackDuckImpactAnalysisTool ONLINE(DirectoryManager directoryManager, CodeLocationNameManager codeLocationNameManager, ImpactAnalysisOptions impactAnalysisOptions, BlackDuckServicesFactory blackDuckServicesFactory,
        EventSystem eventSystem) {
        ImpactAnalysisUploadService impactAnalysisService = ImpactAnalysisUploadService.create(blackDuckServicesFactory);
        BlackDuckService blackDuckService = blackDuckServicesFactory.createBlackDuckService();
        CodeLocationService codeLocationService = blackDuckServicesFactory.createCodeLocationService();
        return new BlackDuckImpactAnalysisTool(directoryManager, codeLocationNameManager, impactAnalysisOptions, eventSystem, impactAnalysisService, blackDuckService, codeLocationService, true);
    }

    public static BlackDuckImpactAnalysisTool OFFLINE(DirectoryManager directoryManager, CodeLocationNameManager codeLocationNameManager, ImpactAnalysisOptions impactAnalysisOptions, EventSystem eventSystem) {
        return new BlackDuckImpactAnalysisTool(directoryManager, codeLocationNameManager, impactAnalysisOptions, eventSystem, null, null, null, false);
    }

    private BlackDuckImpactAnalysisTool(DirectoryManager directoryManager, CodeLocationNameManager codeLocationNameManager, ImpactAnalysisOptions impactAnalysisOptions, EventSystem eventSystem,
        ImpactAnalysisUploadService impactAnalysisUploadService, BlackDuckService blackDuckService, CodeLocationService codeLocationService, boolean online) {
        this.directoryManager = directoryManager;
        this.codeLocationNameManager = codeLocationNameManager;
        this.impactAnalysisOptions = impactAnalysisOptions;
        this.eventSystem = eventSystem;
        this.impactAnalysisUploadService = impactAnalysisUploadService;
        this.blackDuckService = blackDuckService;
        this.codeLocationService = codeLocationService;
        this.online = online;
    }

    public boolean shouldRun() {
        return Boolean.TRUE.equals(impactAnalysisOptions.isEnabled());
    }

    /**
     * @param projectNameAndVersion is the Black Duck project name and version.
     * @param projectVersionWrapper is Nullable, but is a pre-requisite for code location mapping.
     */
    @NotNull
    public ImpactAnalysisToolResult performImpactAnalysisActions(NameVersion projectNameAndVersion, @Nullable ProjectVersionWrapper projectVersionWrapper) throws DetectUserFriendlyException {
        File sourceDirectory = directoryManager.getSourceDirectory();
        String projectName = projectNameAndVersion.getName();
        String projectVersionName = projectNameAndVersion.getVersion();
        String codeLocationPrefix = impactAnalysisOptions.getCodeLocationPrefix();
        String codeLocationSuffix = impactAnalysisOptions.getCodeLocationSuffix();
        String codeLocationName = codeLocationNameManager.createImpactAnalysisCodeLocationName(sourceDirectory, projectName, projectVersionName, codeLocationPrefix, codeLocationSuffix);

        Path impactAnalysisPath;
        try {
            impactAnalysisPath = generateImpactAnalysis(codeLocationName);
        } catch (IOException e) {
            return failImpactAnalysis(e.getMessage());
        }

        if (online && projectVersionWrapper != null) {
            if (impactAnalysisPath != null && impactAnalysisPath.toFile().isFile() && impactAnalysisPath.toFile().canRead()) {
                CodeLocationCreationData<ImpactAnalysisBatchOutput> codeLocationCreationData = uploadImpactAnalysis(impactAnalysisPath, projectNameAndVersion, codeLocationName);
                return mapCodeLocations(impactAnalysisPath, codeLocationCreationData, projectVersionWrapper);
            } else {
                return failImpactAnalysis("Impact analysis file did not exist, is not a file or can't be read.");
            }
        } else {
            logger.debug("Skipping report upload.");
            return ImpactAnalysisToolResult.SUCCESS(impactAnalysisPath);
        }
    }

    public Path generateImpactAnalysis(String impactAnalysisCodeLocationName) throws IOException {
        MethodUseAnalyzer analyzer = new MethodUseAnalyzer();
        Path sourceDirectory = directoryManager.getSourceDirectory().toPath();
        Path outputDirectory = directoryManager.getImpactAnalysisOutputDirectory().toPath();
        if (null != impactAnalysisOptions.getOutputDirectory()) {
            outputDirectory = impactAnalysisOptions.getOutputDirectory();
        }
        Path outputReportFile = analyzer.analyze(sourceDirectory, outputDirectory, impactAnalysisCodeLocationName);
        logger.info(String.format("Vulnerability Impact Analysis generated report at %s", outputReportFile));
        return outputReportFile;
    }

    public CodeLocationCreationData<ImpactAnalysisBatchOutput> uploadImpactAnalysis(Path impactAnalysisPath, NameVersion projectNameVersion, String codeLocationName)
        throws DetectUserFriendlyException {
        ImpactAnalysis impactAnalysis = new ImpactAnalysis(impactAnalysisPath, projectNameVersion.getName(), projectNameVersion.getVersion(), codeLocationName);
        try {
            logger.info(String.format("Preparing to upload impact analysis file: %s", codeLocationName));
            CodeLocationCreationData<ImpactAnalysisBatchOutput> codeLocationCreationData = impactAnalysisUploadService.uploadImpactAnalysis(impactAnalysis);

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

    // TODO: Create a code location mapping service generic enough for all tools.
    private ImpactAnalysisToolResult mapCodeLocations(Path impactAnalysisPath, CodeLocationCreationData<ImpactAnalysisBatchOutput> codeLocationCreationData, ProjectVersionWrapper projectVersionWrapper) {
        for (ImpactAnalysisOutput output : codeLocationCreationData.getOutput().getOutputs()) {
            ImpactAnalysisUploadView impactAnalysisUploadView = output.getImpactAnalysisUploadView();
            ProjectView projectView = projectVersionWrapper.getProjectView();
            ProjectVersionView projectVersionView = projectVersionWrapper.getProjectVersionView();
            Optional<String> projectVersionUrl = projectVersionView.getHref();
            Optional<String> codeLocationUrl = impactAnalysisUploadView.getFirstLink(ImpactAnalysisUploadView.CODE_LOCATION_LINK);

            try {
                if (projectVersionUrl.isPresent() && codeLocationUrl.isPresent()) {
                    logger.info(String.format("Mapping code location %s to project \"%s\" version \"%s\".", codeLocationUrl.get(), projectView.getName(), projectVersionView.getVersionName()));
                    mapCodeLocation(projectVersionUrl.get(), codeLocationUrl.get());
                    logger.info("Successfully mapped code location");
                } else {
                    throw new IntegrationException("Failed to map the code location. Missing code location or project version url.");
                }
            } catch (IntegrationException e) {
                return failImpactAnalysis(e.getMessage());
            }
        }

        return ImpactAnalysisToolResult.SUCCESS(codeLocationCreationData, impactAnalysisPath);
    }

    // TODO: Use the method provided in blackduck-common:49.2.0
    private void mapCodeLocation(String projectVersionUrl, String codeLocationUrl) throws IntegrationException {
        // Retrieving a Code Location with just the Project Code Scanner role is not possible so we must construct it ourselves.
        CodeLocationView codeLocationView = new CodeLocationView();
        codeLocationView.setUrl(codeLocationUrl);
        codeLocationView.setMappedProjectVersion(projectVersionUrl);
        blackDuckService.put(codeLocationView);
    }

    private ImpactAnalysisToolResult failImpactAnalysis(String issueMessage) {
        logger.warn(issueMessage);
        eventSystem.publishEvent(Event.StatusSummary, new Status(STATUS_KEY, StatusType.FAILURE));
        eventSystem.publishEvent(Event.Issue, new DetectIssue(DetectIssueType.IMPACT_ANALYSIS, Collections.singletonList(issueMessage)));
        eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_BLACKDUCK_FEATURE_ERROR, STATUS_KEY));
        return ImpactAnalysisToolResult.FAILURE();
    }
}
