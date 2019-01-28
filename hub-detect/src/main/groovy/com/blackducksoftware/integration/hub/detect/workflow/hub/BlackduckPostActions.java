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
package com.blackducksoftware.integration.hub.detect.workflow.hub;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.status.StatusType;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.codelocation.CodeLocationWaitResult;
import com.synopsys.integration.blackduck.exception.BlackDuckTimeoutExceededException;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.ReportService;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.rest.exception.IntegrationRestException;

public class BlackduckPostActions {
    private final Logger logger = LoggerFactory.getLogger(BlackduckPostActions.class);
    private final BlackDuckServicesFactory blackDuckServicesFactory;
    private final EventSystem eventSystem;

    public BlackduckPostActions(final BlackDuckServicesFactory blackDuckServicesFactory, EventSystem eventSystem) {
        this.blackDuckServicesFactory = blackDuckServicesFactory;
        this.eventSystem = eventSystem;
    }

    public void perform(BlackduckReportOptions blackduckReportOptions, PolicyCheckOptions policyCheckOptions, CodeLocationWaitData codeLocationWaitData, ProjectVersionWrapper projectVersionWrapper, long timeoutInSeconds)
        throws DetectUserFriendlyException {
        try {
            ProjectView projectView = projectVersionWrapper.getProjectView();
            ProjectVersionView projectVersionView = projectVersionWrapper.getProjectVersionView();

            if (policyCheckOptions.shouldPerformPolicyCheck() || blackduckReportOptions.shouldGenerateAnyReport()) {
                logger.info("Detect must wait for bom tool calculations to finish.");
                CodeLocationCreationService codeLocationCreationService = blackDuckServicesFactory.createCodeLocationCreationService();
                List<CodeLocationWaitResult> results = new ArrayList<>();
                if (codeLocationWaitData.hasBdioResults()) {
                    CodeLocationWaitResult result = codeLocationCreationService.waitForCodeLocations(codeLocationWaitData.getBdioUploadRange(), codeLocationWaitData.getBdioUploadCodeLocationNames(), timeoutInSeconds);
                    results.add(result);
                }
                if (codeLocationWaitData.hasScanResults()) {
                    CodeLocationWaitResult result = codeLocationCreationService.waitForCodeLocations(codeLocationWaitData.getSignatureScanRange(), codeLocationWaitData.getSignatureScanCodeLocationNames(), timeoutInSeconds);
                    results.add(result);
                }
                if (codeLocationWaitData.hasBinaryScanResults()) {
                    CodeLocationWaitResult result = codeLocationCreationService.waitForCodeLocations(codeLocationWaitData.getBinaryScanRange(), codeLocationWaitData.getBinaryScanCodeLocationNames(), timeoutInSeconds);
                    results.add(result);
                }
                for (CodeLocationWaitResult result : results) {
                    if (result.getStatus() == CodeLocationWaitResult.Status.PARTIAL) {
                        throw new DetectUserFriendlyException(result.getErrorMessage().orElse("Timed out waiting for code locations to finish on the Black Duck server."), ExitCodeType.FAILURE_TIMEOUT);
                    }
                }
            }

            if (policyCheckOptions.shouldPerformPolicyCheck()) {
                logger.info("Detect will check policy for violations.");
                PolicyChecker policyChecker = new PolicyChecker(eventSystem);
                policyChecker.checkPolicy(policyCheckOptions.getSeveritiesToFailPolicyCheck(), blackDuckServicesFactory.createProjectService(), projectVersionView);
            }

            if (blackduckReportOptions.shouldGenerateAnyReport()) {
                ReportService reportService = blackDuckServicesFactory.createReportService(timeoutInSeconds);
                if (blackduckReportOptions.shouldGenerateRiskReport()) {
                    logger.info("Creating risk report pdf");
                    File reportDirectory = new File(blackduckReportOptions.getRiskReportPdfPath());
                    File createdPdf = reportService.createReportPdfFile(reportDirectory, projectView, projectVersionView);
                    logger.info(String.format("Created risk report pdf: %s", createdPdf.getCanonicalPath()));
                }

                if (blackduckReportOptions.shouldGenerateNoticesReport()) {
                    logger.info("Creating notices report");
                    File noticesDirectory = new File(blackduckReportOptions.getNoticesReportPath());
                    final File noticesFile = reportService.createNoticesReportFile(noticesDirectory, projectView, projectVersionView);
                    logger.info(String.format("Created notices report: %s", noticesFile.getCanonicalPath()));
                }
            }
        } catch (final IllegalArgumentException e) {
            throw new DetectUserFriendlyException(String.format("Your Black Duck configuration is not valid: %s", e.getMessage()), e, ExitCodeType.FAILURE_HUB_CONNECTIVITY);
        } catch (final IntegrationRestException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_HUB_CONNECTIVITY);
        } catch (final BlackDuckTimeoutExceededException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_TIMEOUT);
        } catch (final Exception e) {
            throw new DetectUserFriendlyException(String.format("There was a problem: %s", e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }
}
