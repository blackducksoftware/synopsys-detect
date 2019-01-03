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
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.hub.HubServiceManager;
import com.blackducksoftware.integration.hub.detect.lifecycle.shutdown.ExitCodeRequest;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.exception.BlackDuckTimeoutExceededException;
import com.synopsys.integration.blackduck.service.ProjectService;
import com.synopsys.integration.blackduck.service.ReportService;
import com.synopsys.integration.blackduck.service.model.PolicyStatusDescription;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.exception.IntegrationRestException;

public class HubManager {
    private final Logger logger = LoggerFactory.getLogger(HubManager.class);

    private final DetectConfiguration detectConfiguration;
    private final HubServiceManager hubServiceManager;
    private final PolicyChecker policyChecker;
    private final EventSystem eventSystem;

    public HubManager(final DetectConfiguration detectConfiguration, final HubServiceManager hubServiceManager, final PolicyChecker policyChecker, final EventSystem eventSystem) {
        this.detectConfiguration = detectConfiguration;
        this.hubServiceManager = hubServiceManager;
        this.policyChecker = policyChecker;
        this.eventSystem = eventSystem;
    }

    public void performPostHubActions(ProjectVersionWrapper projectVersionWrapper, CodeLocationWaitData codeLocationWaitData) throws DetectUserFriendlyException {
        try {
            ProjectView projectView = projectVersionWrapper.getProjectView();
            ProjectVersionView projectVersionView = projectVersionWrapper.getProjectVersionView();

            final ProjectService projectService = hubServiceManager.createProjectService();
            final ReportService reportService = hubServiceManager.createReportService();
            final CodeLocationCreationService codeLocationCreationService = hubServiceManager.createCodeLocationCreationService();

            String policyCheckFailOnSeverities = detectConfiguration.getProperty(DetectProperty.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES, PropertyAuthority.None);
            boolean runRiskReport = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_RISK_REPORT_PDF, PropertyAuthority.None);
            boolean runNoticesReport = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_NOTICES_REPORT, PropertyAuthority.None);
            long timeout = detectConfiguration.getLongProperty(DetectProperty.DETECT_API_TIMEOUT, PropertyAuthority.None);
            long timeoutInSeconds = timeout / 1000;
            if (StringUtils.isNotBlank(policyCheckFailOnSeverities) || runRiskReport || runNoticesReport) {
                waitForBomUpdate(codeLocationCreationService, codeLocationWaitData, timeoutInSeconds);
            }

            if (StringUtils.isNotBlank(policyCheckFailOnSeverities)) {
                final Optional<PolicyStatusDescription> optionalPolicyStatusDescription = policyChecker.getPolicyStatus(projectService, projectVersionView);
                if (optionalPolicyStatusDescription.isPresent()) {
                    PolicyStatusDescription policyStatusDescription = optionalPolicyStatusDescription.get();
                    logger.info(policyStatusDescription.getPolicyStatusMessage());
                    if (policyChecker.policyViolated(policyStatusDescription)) {
                        eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_POLICY_VIOLATION, policyStatusDescription.getPolicyStatusMessage()));
                    }
                } else {
                    String availableLinks = StringUtils.join(projectVersionView.getAvailableLinks(), ", ");
                    logger.warn("It is not possible to check the policy status for this project/version. The policy-status link must be present. The available links are: " + availableLinks);
                }
            }

            if (runRiskReport) {
                logger.info("Creating risk report pdf");
                File reportDirectory = new File(detectConfiguration.getProperty(DetectProperty.DETECT_RISK_REPORT_PDF_PATH, PropertyAuthority.None));
                File createdPdf = reportService.createReportPdfFile(reportDirectory, projectView, projectVersionView);
                logger.info(String.format("Created risk report pdf: %s", createdPdf.getCanonicalPath()));
            }

            if (runNoticesReport) {
                logger.info("Creating notices report");
                File noticesDirectory = new File(detectConfiguration.getProperty(DetectProperty.DETECT_NOTICES_REPORT_PATH, PropertyAuthority.None));
                final File noticesFile = reportService.createNoticesReportFile(noticesDirectory, projectView, projectVersionView);
                logger.info(String.format("Created notices report: %s", noticesFile.getCanonicalPath()));
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

    private void waitForBomUpdate(final CodeLocationCreationService codeLocationCreationService, CodeLocationWaitData codeLocationWaitData, long timeoutInSeconds) throws IntegrationException, InterruptedException {
        if (codeLocationWaitData.hasBdioResults()) {
            codeLocationCreationService.waitForCodeLocations(codeLocationWaitData.getBdioUploadRange(), codeLocationWaitData.getBdioUploadCodeLocationNames(), timeoutInSeconds);
        }
        if (codeLocationWaitData.hasScanResults()) {
            codeLocationCreationService.waitForCodeLocations(codeLocationWaitData.getSignatureScanRange(), codeLocationWaitData.getSignatureScanCodeLocationNames(), timeoutInSeconds);
        }
    }
}
