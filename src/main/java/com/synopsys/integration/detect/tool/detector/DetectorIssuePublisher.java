package com.synopsys.integration.detect.tool.detector;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detect.workflow.report.ExceptionUtil;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detector.accuracy.DetectableEvaluationResult;
import com.synopsys.integration.detector.accuracy.DetectorRuleEvaluation;

public class DetectorIssuePublisher {
    public void publishIssues(StatusEventPublisher statusEventPublisher, List<DetectorRuleEvaluation> evaluations) {
        String spacer = "\t";
        for (DetectorRuleEvaluation evaluation : evaluations) {
            if (!evaluation.wasExtractionSuccessful()) {
                List<String> messages = new ArrayList<>();
                if (evaluation.getExtractedDetectableEvaluation().isPresent()) {
                    DetectableEvaluationResult extracted = evaluation.getExtractedDetectableEvaluation().get();
                    Extraction extraction = extracted.getExtraction();
                    if (extraction.getResult() == Extraction.ExtractionResultType.FAILURE) {
                        messages.add("Failure: " + extracted.getDetectableDefinition().getName());
                        messages.add(spacer + extraction.getDescription());
                    } else if (extraction.getResult() == Extraction.ExtractionResultType.EXCEPTION) {
                        messages.add("Exception: " + extracted.getDetectableDefinition().getName());
                        messages.add(spacer + ExceptionUtil.oneSentenceDescription(extraction.getError()));
                    }
                } else {
                    evaluation.getSelectedEntryPointEvaluation().getEvaluatedDetectables().forEach(detectable -> {
                        messages.add("Not Extractable: " + detectable.getDetectableDefinition().getName());
                        detectable.getExplanations().forEach(explanation -> {
                            messages.add(spacer + explanation.describeSelf());
                        });
                    });
                }
                statusEventPublisher.publishIssue(new DetectIssue(DetectIssueType.DETECTOR, "Detector Issue", messages));
            }
        }
    }

}
