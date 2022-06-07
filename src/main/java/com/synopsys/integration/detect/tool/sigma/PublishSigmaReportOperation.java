package com.synopsys.integration.detect.tool.sigma;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusType;

public class PublishSigmaReportOperation {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExitCodePublisher exitCodePublisher;
    private final StatusEventPublisher statusEventPublisher;

    public PublishSigmaReportOperation(ExitCodePublisher exitCodePublisher, StatusEventPublisher statusEventPublisher) {
        this.exitCodePublisher = exitCodePublisher;
        this.statusEventPublisher = statusEventPublisher;
    }

    public void publishReports(List<SigmaReport> sigmaReports) {
        logErrors(sigmaReports);
        publishExitCode(sigmaReports);
        publishStatusEvents(sigmaReports);
    }

    private void logErrors(List<SigmaReport> sigmaReports) {
        sigmaReports.stream()
            .filter(report -> report.getErrorMessage().isPresent())
            .forEach(report -> logger.error(String.format("%s for target %s", report.getErrorMessage().get(), report.getScanTarget())));
    }

    private void publishExitCode(List<SigmaReport> sigmaReports) {
        sigmaReports.stream()
            .filter(sigmaReport -> sigmaReport.getErrorMessage().isPresent())
            .findAny()
            .ifPresent(sigmaReport -> exitCodePublisher.publishExitCode(new ExitCodeRequest(ExitCodeType.FAILURE_SIGMA)));
    }

    private void publishStatusEvents(List<SigmaReport> sigmaReports) {
        for (SigmaReport sigmaReport : sigmaReports) {
            if (!sigmaReport.getErrorMessage().isPresent()) {
                statusEventPublisher.publishStatusSummary(new SigmaStatus(sigmaReport.getScanTarget(), StatusType.SUCCESS));
            } else {
                String errorMessage = sigmaReport.getErrorMessage().get();
                statusEventPublisher.publishIssue(new DetectIssue(DetectIssueType.SIGMA, "Sigma", Collections.singletonList(errorMessage)));
                statusEventPublisher.publishStatusSummary(new SigmaStatus(sigmaReport.getScanTarget(), StatusType.FAILURE));
            }
        }
    }
}
