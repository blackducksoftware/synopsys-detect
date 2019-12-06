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
package com.synopsys.integration.detect.workflow.blackduck;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.codelocation.CodeLocationWaitResult;
import com.synopsys.integration.blackduck.exception.BlackDuckTimeoutExceededException;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.ReportService;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.rest.exception.IntegrationRestException;

public class BlackDuckPostActions {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final BlackDuckServicesFactory blackDuckServicesFactory;
    private final EventSystem eventSystem;

    public BlackDuckPostActions(final BlackDuckServicesFactory blackDuckServicesFactory, final EventSystem eventSystem) {
        this.blackDuckServicesFactory = blackDuckServicesFactory;
        this.eventSystem = eventSystem;
    }

    public void perform(final BlackDuckPostOptions blackDuckPostOptions, final CodeLocationWaitData codeLocationWaitData, final ProjectVersionWrapper projectVersionWrapper, final long timeoutInSeconds) throws DetectUserFriendlyException {
        try {
            final long timeoutInMillisec = 1000L * timeoutInSeconds;
            final ProjectView projectView = projectVersionWrapper.getProjectView();
            final ProjectVersionView projectVersionView = projectVersionWrapper.getProjectVersionView();

            if (blackDuckPostOptions.shouldWaitForResults()) {
                logger.info("Detect must wait for bom tool calculations to finish.");
                final CodeLocationCreationService codeLocationCreationService = blackDuckServicesFactory.createCodeLocationCreationService();
                if (codeLocationWaitData.getExpectedNotificationCount() > 0) {
                    //TODO fix this when NotificationTaskRange doesn't include task start time
                    //ekerwin - The start time of the task is the earliest time a code location was created.
                    // In order to wait the full timeout, we have to not use that start time and instead use now().
                    final NotificationTaskRange notificationTaskRange = new NotificationTaskRange(System.currentTimeMillis(), codeLocationWaitData.getNotificationRange().getStartDate(),
                        codeLocationWaitData.getNotificationRange().getEndDate());
                    final CodeLocationWaitResult result = codeLocationCreationService
                                                              .waitForCodeLocations(notificationTaskRange, codeLocationWaitData.getCodeLocationNames(), codeLocationWaitData.getExpectedNotificationCount(),
                                                                  timeoutInSeconds);
                    if (result.getStatus() == CodeLocationWaitResult.Status.PARTIAL) {
                        throw new DetectUserFriendlyException(result.getErrorMessage().orElse("Timed out waiting for code locations to finish on the Black Duck server."), ExitCodeType.FAILURE_TIMEOUT);
                    }
                }
            }

            if (blackDuckPostOptions.shouldPerformPolicyCheck()) {
                logger.info("Detect will check policy for violations.");
                final PolicyChecker policyChecker = new PolicyChecker(eventSystem);
                policyChecker.checkPolicy(blackDuckPostOptions.getSeveritiesToFailPolicyCheck(), blackDuckServicesFactory.createProjectBomService(), projectVersionView);
            }

            if (blackDuckPostOptions.shouldGenerateAnyReport()) {
                final ReportService reportService = blackDuckServicesFactory.createReportService(timeoutInMillisec);
                if (blackDuckPostOptions.shouldGenerateRiskReport()) {
                    logger.info("Creating risk report pdf");
                    final File reportDirectory = new File(blackDuckPostOptions.getRiskReportPdfPath());

                    if (!reportDirectory.exists() && !reportDirectory.mkdirs()) {
                        logger.warn(String.format("Failed to create risk report pdf directory: %s", blackDuckPostOptions.getRiskReportPdfPath()));
                    }

                    DetectFontLoader detectFontLoader = new DetectFontLoader();
                    final File createdPdf = reportService.createReportPdfFile(reportDirectory, projectView, projectVersionView, detectFontLoader::loadFont, detectFontLoader::loadBoldFont);
                    logger.info(String.format("Created risk report pdf: %s", createdPdf.getCanonicalPath()));
                }

                if (blackDuckPostOptions.shouldGenerateNoticesReport()) {
                    logger.info("Creating notices report");
                    final File noticesDirectory = new File(blackDuckPostOptions.getNoticesReportPath());

                    if (!noticesDirectory.exists() && !noticesDirectory.mkdirs()) {
                        logger.warn(String.format("Failed to create notices directory at %s", blackDuckPostOptions.getNoticesReportPath()));
                    }

                    final File noticesFile = reportService.createNoticesReportFile(noticesDirectory, projectView, projectVersionView);
                    logger.info(String.format("Created notices report: %s", noticesFile.getCanonicalPath()));
                }
            }
        } catch (final DetectUserFriendlyException e) {
            throw e;
        } catch (final IllegalArgumentException e) {
            throw new DetectUserFriendlyException(String.format("Your Black Duck configuration is not valid: %s", e.getMessage()), e, ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
        } catch (final IntegrationRestException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
        } catch (final BlackDuckTimeoutExceededException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_TIMEOUT);
        } catch (final Exception e) {
            throw new DetectUserFriendlyException(String.format("There was a problem: %s", e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }
}
