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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.synopsys.integration.blackduck.api.core.ResourceMetadata;
import com.synopsys.integration.blackduck.api.generated.view.CodeLocationView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.dataservice.CodeLocationService;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
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
import com.synopsys.integration.rest.HttpUrl;
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
    private final BlackDuckApiClient blackDuckService;
    private final CodeLocationService codeLocationService; // TODO: Use this when upgrading to blackduck-common:49.2.0
    private final boolean online;

    public static BlackDuckImpactAnalysisTool ONLINE(DirectoryManager directoryManager, CodeLocationNameManager codeLocationNameManager, ImpactAnalysisOptions impactAnalysisOptions, BlackDuckServicesFactory blackDuckServicesFactory,
        EventSystem eventSystem) {
        ImpactAnalysisUploadService impactAnalysisService = ImpactAnalysisUploadService.create(blackDuckServicesFactory);
        BlackDuckApiClient blackDuckService = blackDuckServicesFactory.getBlackDuckApiClient();
        CodeLocationService codeLocationService = blackDuckServicesFactory.createCodeLocationService();
        return new BlackDuckImpactAnalysisTool(directoryManager, codeLocationNameManager, impactAnalysisOptions, eventSystem, impactAnalysisService, blackDuckService, codeLocationService, true);
    }

    public static BlackDuckImpactAnalysisTool OFFLINE(DirectoryManager directoryManager, CodeLocationNameManager codeLocationNameManager, ImpactAnalysisOptions impactAnalysisOptions, EventSystem eventSystem) {
        return new BlackDuckImpactAnalysisTool(directoryManager, codeLocationNameManager, impactAnalysisOptions, eventSystem, null, null, null, false);
    }

    private BlackDuckImpactAnalysisTool(DirectoryManager directoryManager, CodeLocationNameManager codeLocationNameManager, ImpactAnalysisOptions impactAnalysisOptions, EventSystem eventSystem,
        ImpactAnalysisUploadService impactAnalysisUploadService, BlackDuckApiClient blackDuckService, CodeLocationService codeLocationService, boolean online) {
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

        Path outputDirectory = directoryManager.getImpactAnalysisOutputDirectory().toPath();
        if (null != impactAnalysisOptions.getOutputDirectory()) {
            outputDirectory = impactAnalysisOptions.getOutputDirectory();
        }

        Path impactAnalysisPath;
        try {
            impactAnalysisPath = generateImpactAnalysis(codeLocationName, outputDirectory);
            cleanupTempFiles();
        } catch (IOException e) {
            return failImpactAnalysis(e.getMessage());
        }

        if (impactAnalysisPath == null || !impactAnalysisPath.toFile().isFile() || !impactAnalysisPath.toFile().canRead()) {
            return failImpactAnalysis("Impact analysis file did not exist, is not a file or can't be read.");
        }

        if (!online || projectVersionWrapper == null) {
            logger.debug("Not online. Skipping Impact Analysis report upload.");
            return ImpactAnalysisToolResult.SUCCESS(impactAnalysisPath);
        }

        try {
            CodeLocationCreationData<ImpactAnalysisBatchOutput> codeLocationCreationData = uploadImpactAnalysis(impactAnalysisPath, projectNameAndVersion, codeLocationName);
            ImpactAnalysisToolResult impactAnalysisToolResult = mapCodeLocations(impactAnalysisPath, codeLocationCreationData, projectVersionWrapper);
            eventSystem.publishEvent(Event.StatusSummary, new Status(STATUS_KEY, StatusType.SUCCESS));
            return impactAnalysisToolResult;
        } catch (IntegrationException exception) {
            logger.error(String.format("Failed to upload and map the Impact Analysis code location: %s", exception.getMessage()));
            eventSystem.publishEvent(Event.StatusSummary, new Status(STATUS_KEY, StatusType.FAILURE));
            eventSystem.publishEvent(Event.Issue, new DetectIssue(DetectIssueType.EXCEPTION, Collections.singletonList(exception.getMessage())));
            throw new DetectUserFriendlyException("Failed to upload impact analysis file.", exception, ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
        }
    }

    // TODO: Stop doing this once the impact analysis library allows us to specify a working directory. See IDETECT-2185.
    private void cleanupTempFiles() throws IOException {
        // Impact Analysis generates temporary directories which need to be moved into directories under Detect control for cleanup.
        String tempDirectoryPrefix = "blackduck-method-uses";
        Path tempDirectory = Files.createTempDirectory(tempDirectoryPrefix);

        try (Stream<Path> stream = Files.walk(tempDirectory.getParent(), 1)) {
            stream.filter(tempPath -> tempPath.getFileName().toString().startsWith(tempDirectoryPrefix))
                .forEach(tempPath -> FileUtils.deleteQuietly(tempPath.toFile()));
        } catch (Exception ignore) {
            // We won't notify the user that we failed to move a temp file for cleanup.
        }
    }

    public Path generateImpactAnalysis(String impactAnalysisCodeLocationName, Path outputDirectory) throws IOException {
        MethodUseAnalyzer analyzer = new MethodUseAnalyzer();
        Path sourceDirectory = directoryManager.getSourceDirectory().toPath();
        Path outputReportFile = analyzer.analyze(sourceDirectory, outputDirectory, impactAnalysisCodeLocationName);
        logger.info(String.format("Vulnerability Impact Analysis generated report at %s", outputReportFile));
        return outputReportFile;
    }

    public CodeLocationCreationData<ImpactAnalysisBatchOutput> uploadImpactAnalysis(Path impactAnalysisPath, NameVersion projectNameVersion, String codeLocationName) throws IntegrationException {
        ImpactAnalysis impactAnalysis = new ImpactAnalysis(impactAnalysisPath, projectNameVersion.getName(), projectNameVersion.getVersion(), codeLocationName);
        CodeLocationCreationData<ImpactAnalysisBatchOutput> codeLocationCreationData = impactAnalysisUploadService.uploadImpactAnalysis(impactAnalysis);
        ImpactAnalysisBatchOutput impactAnalysisBatchOutput = codeLocationCreationData.getOutput();
        impactAnalysisBatchOutput.throwExceptionForError(logger);
        return codeLocationCreationData;

    }

    // TODO: Create a code location mapping service generic enough for all tools.
    private ImpactAnalysisToolResult mapCodeLocations(Path impactAnalysisPath, CodeLocationCreationData<ImpactAnalysisBatchOutput> codeLocationCreationData, ProjectVersionWrapper projectVersionWrapper) throws IntegrationException {
        for (ImpactAnalysisOutput output : codeLocationCreationData.getOutput().getOutputs()) {
            ImpactAnalysisUploadView impactAnalysisUploadView = output.getImpactAnalysisUploadView();
            ProjectView projectView = projectVersionWrapper.getProjectView();
            ProjectVersionView projectVersionView = projectVersionWrapper.getProjectVersionView();
            HttpUrl projectVersionUrl = projectVersionView.getHref();
            HttpUrl codeLocationUrl = impactAnalysisUploadView.getFirstLink(ImpactAnalysisUploadView.CODE_LOCATION_LINK);

            try {
                logger.info(String.format("Mapping code location to project \"%s\" version \"%s\".", projectView.getName(), projectVersionView.getVersionName()));
                mapCodeLocation(projectVersionUrl, codeLocationUrl);
                logger.info("Successfully mapped code location.");
            } catch (IntegrationException e) {
                return failImpactAnalysis(e.getMessage());
            }
        }

        return ImpactAnalysisToolResult.SUCCESS(codeLocationCreationData, impactAnalysisPath);
    }

    // TODO: Use the method provided in blackduck-common:49.2.0
    private void mapCodeLocation(HttpUrl projectVersionUrl, HttpUrl codeLocationUrl) throws IntegrationException {
        // Retrieving a Code Location with just the Project Code Scanner role is not possible so we must construct it ourselves.
        CodeLocationView codeLocationView = new CodeLocationView();

        ResourceMetadata resourceMetadata = new ResourceMetadata();
        resourceMetadata.setHref(codeLocationUrl);
        codeLocationView.setMeta(resourceMetadata);

        NullNode pathJsonNode = new JsonNodeFactory(false).nullNode();
        codeLocationView.setPatch(pathJsonNode);

        codeLocationView.setMappedProjectVersion(projectVersionUrl.string());
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
