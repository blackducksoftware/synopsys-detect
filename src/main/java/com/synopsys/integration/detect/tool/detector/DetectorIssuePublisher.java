package com.synopsys.integration.detect.tool.detector;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detect.tool.detector.report.DetectorDirectoryReport;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;

public class DetectorIssuePublisher {
    public void publishIssues(StatusEventPublisher statusEventPublisher, List<DetectorDirectoryReport> reports) {
        //TODO (detectors): just verify we don't want to publish 'attempted' when successfully extracted, right now publishing all attempted in not-extracted.
        String spacer = "\t";
        for (DetectorDirectoryReport report : reports) {
            report.getNotExtractedDetectors().forEach(notExtracted -> {
                notExtracted.getAttemptedDetectables().forEach(attempted -> {
                    List<String> messages = new ArrayList<>();
                    messages.add(attempted.getStatusCode() + ": " + attempted.getDetectable().getName());
                    messages.add(spacer + attempted.getStatusReason());
                    statusEventPublisher.publishIssue(new DetectIssue(DetectIssueType.DETECTOR, "Detector Issue", messages));
                });
            });

        }
    }

}
