/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
import com.synopsys.integration.blackduck.api.generated.view.CodeLocationView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.view.ScanSummaryView;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.exception.HubTimeoutExceededException;
import com.synopsys.integration.blackduck.service.CodeLocationService;
import com.synopsys.integration.blackduck.service.HubService;
import com.synopsys.integration.blackduck.service.ProjectService;
import com.synopsys.integration.blackduck.service.ReportService;
import com.synopsys.integration.blackduck.service.ScanStatusService;
import com.synopsys.integration.blackduck.service.model.PolicyStatusDescription;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.exception.IntegrationRestException;
import com.synopsys.integration.util.NameVersion;

public class HubManager {
    private final Logger logger = LoggerFactory.getLogger(HubManager.class);

    private final CodeLocationNameManager codeLocationNameManager;
    private final DetectConfiguration detectConfiguration;
    private final HubServiceManager hubServiceManager;
    private final PolicyChecker policyChecker;
    private final EventSystem eventSystem;

    public HubManager(final CodeLocationNameManager codeLocationNameManager, final DetectConfiguration detectConfiguration, final HubServiceManager hubServiceManager,
        final PolicyChecker policyChecker, final EventSystem eventSystem) {
        this.codeLocationNameManager = codeLocationNameManager;
        this.detectConfiguration = detectConfiguration;
        this.hubServiceManager = hubServiceManager;
        this.policyChecker = policyChecker;
        this.eventSystem = eventSystem;
    }

    public void performPostHubActions(final NameVersion projectNameVersion, final ProjectVersionView projectVersionView) throws DetectUserFriendlyException {
        try {
            final ProjectService projectService = hubServiceManager.createProjectService();
            final ReportService reportService = hubServiceManager.createReportService();
            final HubService hubService = hubServiceManager.createHubService();
            final CodeLocationService codeLocationService = hubServiceManager.createCodeLocationService();
            final ScanStatusService scanStatusService = hubServiceManager.createScanStatusService();

            if (StringUtils.isNotBlank(detectConfiguration.getProperty(DetectProperty.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES, PropertyAuthority.None)) || detectConfiguration
                                                                                                                                                              .getBooleanProperty(DetectProperty.DETECT_RISK_REPORT_PDF, PropertyAuthority.None)
                    || detectConfiguration.getBooleanProperty(DetectProperty.DETECT_NOTICES_REPORT, PropertyAuthority.None)) {
                waitForBomUpdate(codeLocationService, hubService, scanStatusService);
            }

            if (StringUtils.isNotBlank(detectConfiguration.getProperty(DetectProperty.DETECT_POLICY_CHECK_FAIL_ON_SEVERITIES, PropertyAuthority.None))) {
                final PolicyStatusDescription policyStatusDescription = policyChecker.getPolicyStatus(projectService, projectVersionView);
                logger.info(policyStatusDescription.getPolicyStatusMessage());
                if (policyChecker.policyViolated(policyStatusDescription)) {
                    eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_POLICY_VIOLATION, policyStatusDescription.getPolicyStatusMessage()));
                }
            }

            if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_RISK_REPORT_PDF, PropertyAuthority.None)) {
                logger.info("Creating risk report pdf");
                final File pdfFile = reportService
                                         .createReportPdfFile(new File(detectConfiguration.getProperty(DetectProperty.DETECT_RISK_REPORT_PDF_PATH, PropertyAuthority.None)), projectNameVersion.getName(), projectNameVersion.getVersion());
                logger.info(String.format("Created risk report pdf: %s", pdfFile.getCanonicalPath()));
            }

            if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_NOTICES_REPORT, PropertyAuthority.None)) {
                logger.info("Creating notices report");
                final File noticesFile = reportService.createNoticesReportFile(new File(detectConfiguration.getProperty(DetectProperty.DETECT_NOTICES_REPORT_PATH, PropertyAuthority.None)), projectNameVersion.getName(),
                    projectNameVersion.getVersion()
                );
                if (noticesFile != null) {
                    logger.info(String.format("Created notices report: %s", noticesFile.getCanonicalPath()));
                }
            }
        } catch (final IllegalStateException e) {
            throw new DetectUserFriendlyException(String.format("Your Black Duck configuration is not valid: %s", e.getMessage()), e, ExitCodeType.FAILURE_HUB_CONNECTIVITY);
        } catch (final IntegrationRestException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_HUB_CONNECTIVITY);
        } catch (final HubTimeoutExceededException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_TIMEOUT);
        } catch (final Exception e) {
            throw new DetectUserFriendlyException(String.format("There was a problem: %s", e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }

    private void waitForBomUpdate(final CodeLocationService codeLocationService, final HubService hubService, final CodeLocationCreationService codeLocationCreationService) throws IntegrationException, InterruptedException {
        final List<CodeLocationView> allCodeLocations = new ArrayList<>();
        for (final String codeLocationName : codeLocationNameManager.getCodeLocationNames()) {
            final CodeLocationView codeLocationView = codeLocationService.getCodeLocationByName(codeLocationName);
            allCodeLocations.add(codeLocationView);
        }
        final List<ScanSummaryView> scanSummaryViews = new ArrayList<>();
        for (final CodeLocationView codeLocationView : allCodeLocations) {
            final String scansLink = hubService.getFirstLinkSafely(codeLocationView, CodeLocationView.SCANS_LINK);
            if (StringUtils.isNotBlank(scansLink)) {
                final List<ScanSummaryView> codeLocationScanSummaryViews = hubService.getResponses(scansLink, ScanSummaryView.class, true);
                scanSummaryViews.addAll(codeLocationScanSummaryViews);
            }
        }
        logger.info("Waiting for the BOM to be updated");
        codeLocationCreationService.assertScansFinished(scanSummaryViews);
        logger.info("The BOM has been updated");
    }
}
