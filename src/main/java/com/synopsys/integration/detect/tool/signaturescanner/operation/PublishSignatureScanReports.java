package com.synopsys.integration.detect.tool.signaturescanner.operation;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerReport;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;
import com.synopsys.integration.detect.workflow.status.SignatureScanStatus;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusType;

public class PublishSignatureScanReports {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExitCodePublisher exitCodePublisher;
    private final StatusEventPublisher statusEventPublisher;

    public PublishSignatureScanReports(final ExitCodePublisher exitCodePublisher, final StatusEventPublisher statusEventPublisher) {
        this.exitCodePublisher = exitCodePublisher;
        this.statusEventPublisher = statusEventPublisher;
    }

    public void publishReports(List<SignatureScannerReport> signatureScannerReports) {
        signatureScannerReports.forEach(this::publishReport);

        signatureScannerReports.stream()
            .filter(SignatureScannerReport::isFailure)
            .findAny()
            .ifPresent(report -> {
                logger.error(String.format("The Signature Scanner encountered an error%s. Please refer to Black Duck documentation or contact support.", report.getExitCode().map(code -> " (" + code + ")").orElse(".")));
                exitCodePublisher.publishExitCode(new ExitCodeRequest(ExitCodeType.FAILURE_SCAN));
            });
    }

    private void publishReport(SignatureScannerReport signatureScannerReport) {
        if (signatureScannerReport.isSuccessful()) {
            statusEventPublisher.publishStatusSummary(new SignatureScanStatus(signatureScannerReport.getSignatureScanPath().getTargetCanonicalPath(), StatusType.SUCCESS));
            return;
        }

        String scanTargetPath = signatureScannerReport.getSignatureScanPath().getTargetCanonicalPath();
        if (!signatureScannerReport.hasOutput()) {
            String errorMessage = String.format("Scanning target %s was never scanned by the BlackDuck CLI.", scanTargetPath);
            logger.info(errorMessage);
            statusEventPublisher.publishIssue(new DetectIssue(DetectIssueType.SIGNATURE_SCANNER, "Black Duck Signature Scanner", Arrays.asList(errorMessage)));
        } else {
            String errorMessage = signatureScannerReport.getErrorMessage()
                                      .map(message -> String.format("Scanning target %s failed: %s", scanTargetPath, message))
                                      .orElse(String.format("Scanning target %s failed for an unknown reason.", scanTargetPath));
            logger.error(errorMessage);
            signatureScannerReport.getException().ifPresent(exception -> logger.debug(errorMessage, exception));

            statusEventPublisher.publishIssue(new DetectIssue(DetectIssueType.SIGNATURE_SCANNER, "Black Duck Signature Scanner", Arrays.asList(errorMessage)));
        }

        statusEventPublisher.publishStatusSummary(new SignatureScanStatus(signatureScannerReport.getSignatureScanPath().getTargetCanonicalPath(), StatusType.FAILURE));
    }
}
