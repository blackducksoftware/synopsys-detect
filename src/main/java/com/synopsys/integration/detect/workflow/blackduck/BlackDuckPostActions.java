/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.codelocation.CodeLocationWaitResult;
import com.synopsys.integration.blackduck.exception.BlackDuckTimeoutExceededException;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.dataservice.ProjectBomService;
import com.synopsys.integration.blackduck.service.dataservice.ReportService;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationWaitData;
import com.synopsys.integration.detect.workflow.blackduck.policy.PolicyChecker;
import com.synopsys.integration.detect.workflow.result.ReportDetectResult;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.exception.IntegrationRestException;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckPostActions {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CodeLocationCreationService codeLocationCreationService;
    private final StatusEventPublisher statusEventPublisher;
    private final ExitCodePublisher exitCodePublisher;
    private final BlackDuckApiClient blackDuckApiClient;
    private final ProjectBomService projectBomService;
    private final ReportService reportService;
    private final OperationSystem operationSystem;
    private final DetectFontLoader detectFontLoader;

    public BlackDuckPostActions(CodeLocationCreationService codeLocationCreationService, StatusEventPublisher statusEventPublisher, ExitCodePublisher exitCodePublisher, BlackDuckApiClient blackDuckApiClient,
        ProjectBomService projectBomService, ReportService reportService, OperationSystem operationSystem, DetectFontLoader detectFontLoader) {
        this.codeLocationCreationService = codeLocationCreationService;
        this.statusEventPublisher = statusEventPublisher;
        this.exitCodePublisher = exitCodePublisher;
        this.blackDuckApiClient = blackDuckApiClient;
        this.projectBomService = projectBomService;
        this.reportService = reportService;
        this.operationSystem = operationSystem;
        this.detectFontLoader = detectFontLoader;
    }

    public void perform(BlackDuckPostOptions blackDuckPostOptions, CodeLocationWaitData codeLocationWaitData, ProjectVersionWrapper projectVersionWrapper, NameVersion projectNameVersion, long timeoutInSeconds)
        throws DetectUserFriendlyException {

        String currentOperationKey = null;
        try {
            if (blackDuckPostOptions.shouldWaitForResults()) {
                currentOperationKey = "Black Duck Wait for Code Locations";
                operationSystem.beginOperation(currentOperationKey);
                waitForCodeLocations(codeLocationWaitData, timeoutInSeconds, projectNameVersion);
                operationSystem.completeWithSuccess(currentOperationKey);
            }
            if (blackDuckPostOptions.shouldPerformPolicyCheck()) {
                currentOperationKey = "Black Duck Policy Check";
                operationSystem.beginOperation(currentOperationKey);
                checkPolicy(blackDuckPostOptions, projectVersionWrapper.getProjectVersionView());
                operationSystem.completeWithSuccess(currentOperationKey);
            }
            if (blackDuckPostOptions.shouldGenerateAnyReport()) {
                currentOperationKey = "Black Duck Report Generation";
                operationSystem.beginOperation(currentOperationKey);
                generateReports(blackDuckPostOptions, projectVersionWrapper);
                operationSystem.completeWithSuccess(currentOperationKey);
            }
        } catch (DetectUserFriendlyException e) {
            operationSystem.completeWithError(currentOperationKey, e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            String errorReason = String.format("Your Black Duck configuration is not valid: %s", e.getMessage());
            operationSystem.completeWithError(currentOperationKey, errorReason);
            throw new DetectUserFriendlyException(errorReason, e, ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
        } catch (IntegrationRestException e) {
            operationSystem.completeWithError(currentOperationKey, e.getMessage());
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
        } catch (BlackDuckTimeoutExceededException e) {
            operationSystem.completeWithError(currentOperationKey, e.getMessage());
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_TIMEOUT);
        } catch (InterruptedException e) {
            String errorReason = String.format("There was a problem: %s", e.getMessage());
            operationSystem.completeWithError(currentOperationKey, errorReason);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            throw new DetectUserFriendlyException(errorReason, e, ExitCodeType.FAILURE_GENERAL_ERROR);
        } catch (Exception e) {
            String errorReason = String.format("There was a problem: %s", e.getMessage());
            operationSystem.completeWithError(currentOperationKey, errorReason);
            throw new DetectUserFriendlyException(errorReason, e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }

    private void waitForCodeLocations(CodeLocationWaitData codeLocationWaitData, long timeoutInSeconds, NameVersion projectNameVersion) throws DetectUserFriendlyException, InterruptedException, IntegrationException {
        logger.info("Detect must wait for bom tool calculations to finish.");
        if (codeLocationWaitData.getExpectedNotificationCount() > 0) {
            //TODO fix this when NotificationTaskRange doesn't include task start time
            //ekerwin - The start time of the task is the earliest time a code location was created.
            // In order to wait the full timeout, we have to not use that start time and instead use now().
            //TODO: Handle the possible null pointer here.
            NotificationTaskRange notificationTaskRange = new NotificationTaskRange(System.currentTimeMillis(), codeLocationWaitData.getNotificationRange().getStartDate(),
                codeLocationWaitData.getNotificationRange().getEndDate());
            CodeLocationWaitResult result = codeLocationCreationService.waitForCodeLocations(
                notificationTaskRange,
                projectNameVersion,
                codeLocationWaitData.getCodeLocationNames(),
                codeLocationWaitData.getExpectedNotificationCount(),
                timeoutInSeconds
            );
            if (result.getStatus() == CodeLocationWaitResult.Status.PARTIAL) {
                throw new DetectUserFriendlyException(result.getErrorMessage().orElse("Timed out waiting for code locations to finish on the Black Duck server."), ExitCodeType.FAILURE_TIMEOUT);
            }
        }
    }

    private void checkPolicy(BlackDuckPostOptions blackDuckPostOptions, ProjectVersionView projectVersionView) throws IntegrationException {
        logger.info("Detect will check policy for violations.");
        PolicyChecker policyChecker = new PolicyChecker(exitCodePublisher, blackDuckApiClient, projectBomService);
        policyChecker.checkPolicy(blackDuckPostOptions.getSeveritiesToFailPolicyCheck(), projectVersionView);
    }

    private void generateReports(BlackDuckPostOptions blackDuckPostOptions, ProjectVersionWrapper projectVersionWrapper) throws IntegrationException, IOException, InterruptedException {
        ProjectView projectView = projectVersionWrapper.getProjectView();
        ProjectVersionView projectVersionView = projectVersionWrapper.getProjectVersionView();

        if (blackDuckPostOptions.shouldGenerateRiskReport()) {
            logger.info("Creating risk report pdf");
            File reportDirectory = blackDuckPostOptions.getRiskReportPdfPath().toFile();

            if (!reportDirectory.exists() && !reportDirectory.mkdirs()) {
                logger.warn(String.format("Failed to create risk report pdf directory: %s", blackDuckPostOptions.getRiskReportPdfPath().toString()));
            }

            //DetectFontLoader detectFontLoader = new DetectFontLoader();
            File createdPdf = reportService.createReportPdfFile(reportDirectory, projectView, projectVersionView, detectFontLoader::loadFont, detectFontLoader::loadBoldFont);

            logger.info(String.format("Created risk report pdf: %s", createdPdf.getCanonicalPath()));
            statusEventPublisher.publishDetectResult(new ReportDetectResult("Risk Report", createdPdf.getCanonicalPath()));
        }

        if (blackDuckPostOptions.shouldGenerateNoticesReport()) {
            logger.info("Creating notices report");
            File noticesDirectory = blackDuckPostOptions.getNoticesReportPath().toFile();

            if (!noticesDirectory.exists() && !noticesDirectory.mkdirs()) {
                logger.warn(String.format("Failed to create notices directory at %s", blackDuckPostOptions.getNoticesReportPath().toString()));
            }

            File noticesFile = reportService.createNoticesReportFile(noticesDirectory, projectView, projectVersionView);
            logger.info(String.format("Created notices report: %s", noticesFile.getCanonicalPath()));

            statusEventPublisher.publishDetectResult(new ReportDetectResult("Notices Report", noticesFile.getCanonicalPath()));

        }
    }
}
