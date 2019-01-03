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
package com.blackducksoftware.integration.hub.detect.lifecycle.run;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.DetectTool;
import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.hub.HubServiceManager;
import com.blackducksoftware.integration.hub.detect.lifecycle.DetectContext;
import com.blackducksoftware.integration.hub.detect.lifecycle.shutdown.ExitCodeRequest;
import com.blackducksoftware.integration.hub.detect.tool.binaryscanner.BlackDuckBinaryScannerTool;
import com.blackducksoftware.integration.hub.detect.tool.detector.DetectorTool;
import com.blackducksoftware.integration.hub.detect.tool.detector.DetectorToolResult;
import com.blackducksoftware.integration.hub.detect.tool.docker.DockerTool;
import com.blackducksoftware.integration.hub.detect.tool.docker.DockerToolResult;
import com.blackducksoftware.integration.hub.detect.tool.polaris.PolarisTool;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.BlackDuckSignatureScannerTool;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.SignatureScannerToolResult;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.ConnectivityManager;
import com.blackducksoftware.integration.hub.detect.workflow.DetectConfigurationFactory;
import com.blackducksoftware.integration.hub.detect.workflow.DetectToolFilter;
import com.blackducksoftware.integration.hub.detect.workflow.bdio.BdioManager;
import com.blackducksoftware.integration.hub.detect.workflow.bdio.BdioResult;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.BdioCodeLocationCreator;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.hub.CodeLocationWaitData;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectBdioUploadService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectCodeLocationUnmapService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectProjectService;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectProjectServiceOptions;
import com.blackducksoftware.integration.hub.detect.workflow.hub.HubManager;
import com.blackducksoftware.integration.hub.detect.workflow.hub.PolicyChecker;
import com.blackducksoftware.integration.hub.detect.workflow.project.ProjectNameVersionDecider;
import com.blackducksoftware.integration.hub.detect.workflow.project.ProjectNameVersionOptions;
import com.blackducksoftware.integration.hub.detect.workflow.report.util.ReportConstants;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchOptions;
import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatchOutput;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class RunManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectContext detectContext;

    public RunManager(DetectContext detectContext) {
        this.detectContext = detectContext;
    }

    public RunResult run() throws DetectUserFriendlyException, InterruptedException, IntegrationException {
        //TODO: Better way for run manager to get dependencies so he can be tested. (And better ways of creating his objects)
        DetectConfiguration detectConfiguration = detectContext.getBean(DetectConfiguration.class);
        DetectConfigurationFactory detectConfigurationFactory = detectContext.getBean(DetectConfigurationFactory.class);
        DirectoryManager directoryManager = detectContext.getBean(DirectoryManager.class);
        EventSystem eventSystem = detectContext.getBean(EventSystem.class);
        CodeLocationNameManager codeLocationNameManager = detectContext.getBean(CodeLocationNameManager.class);
        BdioCodeLocationCreator bdioCodeLocationCreator = detectContext.getBean(BdioCodeLocationCreator.class);
        ConnectionManager connectionManager = detectContext.getBean(ConnectionManager.class);
        DetectInfo detectInfo = detectContext.getBean(DetectInfo.class);
        ConnectivityManager connectivityManager = detectContext.getBean(ConnectivityManager.class);

        if (connectivityManager.getPhoneHomeManager().isPresent()) {
            connectivityManager.getPhoneHomeManager().get().startPhoneHome();
        }

        RunResult runResult = new RunResult();
        RunOptions runOptions = detectConfigurationFactory.createRunOptions();

        DetectToolFilter detectToolFilter = runOptions.getDetectToolFilter();

        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.DOCKER)) {
            logger.info("Will include the docker tool.");
            DockerTool dockerTool = new DockerTool(detectContext);

            DockerToolResult dockerToolResult = dockerTool.run();
            runResult.addToolNameVersionIfPresent(DetectTool.DOCKER, dockerToolResult.dockerProjectNameVersion);
            runResult.addDetectCodeLocations(dockerToolResult.dockerCodeLocations);
            runResult.addDockerFile(dockerToolResult.dockerTar);

            if (dockerToolResult.resultType == DockerToolResult.DockerToolResultType.FAILURE) {
                eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_GENERAL_ERROR, dockerToolResult.errorMessage));
            }
            logger.info("Docker actions finished.");
        } else {
            logger.info("Docker tool will not be run.");
        }

        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.DETECTOR)) {
            logger.info("Will include the detector tool.");
            String projectBomTool = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_DETECTOR, PropertyAuthority.None);
            SearchOptions searchOptions = detectConfigurationFactory.createSearchOptions(directoryManager.getSourceDirectory());
            DetectorTool detectorTool = new DetectorTool(detectContext);

            DetectorToolResult detectorToolResult = detectorTool.performDetectors(searchOptions, projectBomTool);
            runResult.addToolNameVersionIfPresent(DetectTool.DETECTOR, detectorToolResult.bomToolProjectNameVersion);
            runResult.addDetectCodeLocations(detectorToolResult.bomToolCodeLocations);
            runResult.addApplicableDetectors(detectorToolResult.applicableDetectorTypes);

            if (detectorToolResult.failedDetectorTypes.size() > 0) {
                eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_DETECTOR, "A detector failed."));
            }
            logger.info("Detector actions finished.");
        } else {
            logger.info("Detector tool will not be run.");
        }

        logger.info(ReportConstants.RUN_SEPARATOR);
        logger.info("Completed code location tools.");

        logger.info("Determining project info.");

        ProjectNameVersionOptions projectNameVersionOptions = detectConfigurationFactory.createProjectNameVersionOptions(directoryManager.getSourceDirectory().getName());
        ProjectNameVersionDecider projectNameVersionDecider = new ProjectNameVersionDecider(projectNameVersionOptions);
        NameVersion projectNameVersion = projectNameVersionDecider.decideProjectNameVersion(runOptions.getPreferredTools(), runResult.getDetectToolProjectInfo());

        logger.info("Project name: " + projectNameVersion.getName());
        logger.info("Project version: " + projectNameVersion.getVersion());

        Optional<ProjectVersionWrapper> projectVersionWrapper = Optional.empty();

        if (connectivityManager.isDetectOnline() && connectivityManager.getHubServiceManager().isPresent()) {
            HubServiceManager hubServiceManager = connectivityManager.getHubServiceManager().get();
            logger.info("Getting or creating project.");
            DetectProjectServiceOptions options = detectConfigurationFactory.createDetectProjectServiceOptions();
            DetectProjectService detectProjectService = new DetectProjectService(hubServiceManager, options);
            projectVersionWrapper = Optional.of(detectProjectService.createOrUpdateHubProject(projectNameVersion));
            if (projectVersionWrapper.isPresent() && runOptions.shouldUnmapCodeLocations()) {
                logger.info("Unmapping code locations.");
                DetectCodeLocationUnmapService detectCodeLocationUnmapService = new DetectCodeLocationUnmapService(hubServiceManager.createBlackDuckService(), hubServiceManager.createCodeLocationService());
                detectCodeLocationUnmapService.unmapCodeLocations(projectVersionWrapper.get().getProjectVersionView());
            } else {
                logger.debug("Will not unmap code locations: Project view was not present, or should not unmap code locations.");
            }
        } else {
            logger.debug("Detect is not online, and will not create the project.");
        }

        logger.info("Completed project and version actions.");

        logger.info("Processing Detect Code Locations.");
        CodeLocationWaitData codeLocationWaitData = new CodeLocationWaitData();
        BdioManager bdioManager = new BdioManager(detectInfo, new SimpleBdioFactory(), new IntegrationEscapeUtil(), codeLocationNameManager, detectConfiguration, bdioCodeLocationCreator, directoryManager, eventSystem);
        BdioResult bdioResult = bdioManager.createBdioFiles(runOptions.getAggregateName(), projectNameVersion, runResult.getDetectCodeLocations());

        if (bdioResult.getUploadTargets().size() > 0) {
            logger.info("Created " + bdioResult.getUploadTargets().size() + " BDIO files.");
            bdioResult.getUploadTargets().forEach(it -> eventSystem.publishEvent(Event.OutputFileOfInterest, it.getUploadFile()));
            if (connectivityManager.isDetectOnline() && connectivityManager.getHubServiceManager().isPresent()) {
                logger.info("Uploading BDIO files.");
                HubServiceManager hubServiceManager = connectivityManager.getHubServiceManager().get();
                DetectBdioUploadService detectBdioUploadService = new DetectBdioUploadService(detectConfiguration, hubServiceManager.createBdioUploadService());
                CodeLocationCreationData<UploadBatchOutput> uploadBatchOutputCodeLocationCreationData = detectBdioUploadService.uploadBdioFiles(bdioResult.getUploadTargets());
                codeLocationWaitData.setFromBdioCodeLocationCreationData(uploadBatchOutputCodeLocationCreationData);
            }
        } else {
            logger.debug("Did not create any BDIO files.");
        }

        logger.info("Completed Detect Code Location processing.");

        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.SIGNATURE_SCAN)) {
            logger.info("Will include the signature scanner tool.");
            BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = detectConfigurationFactory.createBlackDuckSignatureScannerOptions();
            BlackDuckSignatureScannerTool blackDuckSignatureScannerTool = new BlackDuckSignatureScannerTool(blackDuckSignatureScannerOptions, detectContext);
            SignatureScannerToolResult signatureScannerToolResult = blackDuckSignatureScannerTool.runScanTool(projectNameVersion, runResult.getDockerTar());
            if (signatureScannerToolResult.getResult() == Result.SUCCESS && signatureScannerToolResult.getCreationData().isPresent()) {
                codeLocationWaitData.setFromSignatureScannerCodeLocationCreationData(signatureScannerToolResult.getCreationData().get());
            }
            logger.info("Signature scanner actions finished.");
        } else {
            logger.info("Signature scan tool will not be run.");
        }

        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.BINARY_SCAN)) {
            logger.info("Will include the binary scanner tool.");
            if (connectivityManager.isDetectOnline() && connectivityManager.getHubServiceManager().isPresent()) {
                HubServiceManager hubServiceManager = connectivityManager.getHubServiceManager().get();
                BlackDuckBinaryScannerTool blackDuckBinaryScanner = new BlackDuckBinaryScannerTool(eventSystem, codeLocationNameManager, detectConfiguration, hubServiceManager);
                blackDuckBinaryScanner.performBinaryScanActions(projectNameVersion);
            }
            logger.info("Binary scanner actions finished.");
        } else {
            logger.info("Binary scan tool will not be run.");
        }

        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.POLARIS)) {
            logger.info("Will include the Polaris tool.");
            PolarisTool polarisTool = new PolarisTool(eventSystem, directoryManager, new ExecutableRunner(), connectionManager);
            polarisTool.runPolaris(new Slf4jIntLogger(logger), directoryManager.getSourceDirectory());
            logger.info("Polaris actions finished.");
        } else {
            logger.info("Polaris CLI tool will not be run.");
        }

        logger.info(ReportConstants.RUN_SEPARATOR);
        if (projectVersionWrapper.isPresent() && connectivityManager.isDetectOnline() && connectivityManager.getHubServiceManager().isPresent()) {
            HubServiceManager hubServiceManager = connectivityManager.getHubServiceManager().get();

            logger.info("Will perform Black Duck post actions.");
            HubManager hubManager = new HubManager(detectConfiguration, hubServiceManager, new PolicyChecker(detectConfiguration), eventSystem);
            hubManager.performPostHubActions(projectVersionWrapper.get(), codeLocationWaitData);

            boolean hasAtLeastOneBdio = !bdioResult.getUploadTargets().isEmpty();
            boolean shouldHaveScanned = detectToolFilter.shouldInclude(DetectTool.SIGNATURE_SCAN);

            if (hasAtLeastOneBdio || shouldHaveScanned) {
                final Optional<String> componentsLink = projectVersionWrapper.get().getProjectVersionView().getFirstLink(ProjectVersionView.COMPONENTS_LINK);
                if (componentsLink.isPresent()) {
                    logger.info(String.format("To see your results, follow the URL: %s", componentsLink.get()));
                }
            }

            logger.info("Black Duck actions have finished.");
        } else {
            logger.debug("Will not perform post actions: Detect is not online.");
        }

        logger.info("All tools have finished.");
        logger.info(ReportConstants.RUN_SEPARATOR);

        return runResult;
    }

}
