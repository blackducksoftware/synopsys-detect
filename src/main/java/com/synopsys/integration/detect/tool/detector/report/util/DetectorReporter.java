package com.synopsys.integration.detect.tool.detector.report.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.tool.detector.report.DetectorDirectoryReport;
import com.synopsys.integration.detect.tool.detector.report.detectable.AttemptedDetectableReport;
import com.synopsys.integration.detect.tool.detector.report.detectable.ExtractedDetectableReport;
import com.synopsys.integration.detect.tool.detector.report.rule.EvaluatedDetectorRuleReport;
import com.synopsys.integration.detect.tool.detector.report.rule.ExtractedDetectorRuleReport;
import com.synopsys.integration.detect.tool.detector.report.rule.NotFoundDetectorRuleReport;
import com.synopsys.integration.detector.accuracy.DetectableEvaluationResult;
import com.synopsys.integration.detector.accuracy.DetectorEvaluation;
import com.synopsys.integration.detector.accuracy.DetectorRuleNotFoundResult;
import com.synopsys.integration.detector.accuracy.DetectorSearchResult;
import com.synopsys.integration.detector.base.DetectorEvaluationUtil;
import com.synopsys.integration.detector.rule.DetectableDefinition;

public class DetectorReporter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<DetectorDirectoryReport> generateReport(DetectorEvaluation detectorEvaluation) {
        List<DetectorDirectoryReport> reports = new ArrayList<>();
        List<DetectorEvaluation> evaluations = DetectorEvaluationUtil.asFlatList(detectorEvaluation);

        evaluations.forEach(evaluation -> {
            List<NotFoundDetectorRuleReport> notFoundDetectors = reportNotFound(evaluation);

            List<ExtractedDetectorRuleReport> extractedDetectors = new ArrayList<>();
            List<EvaluatedDetectorRuleReport> notExtractedDetectors = new ArrayList<>();

            evaluation.getFoundDetectorRuleEvaluations().forEach(ruleEvaluation -> {
                List<AttemptedDetectableReport> notFoundEntryPoints = new ArrayList<>();
                ruleEvaluation.getNotFoundEntryPoints().forEach(notFoundEntry -> {
                    notFoundEntryPoints.add(AttemptedDetectableReport.notApplicable(notFoundEntry.getEntryPoint().getPrimary(), notFoundEntry.getApplicableResult()));
                });

                List<AttemptedDetectableReport> attemptedDetectables = new ArrayList<>();
                List<DetectableEvaluationResult> evaluatedDetectables = ruleEvaluation.getEvaluatedEntryPoint().getEvaluatedDetectables();
                DetectableEvaluationResult extracted = null;
                for (DetectableEvaluationResult evaluated : evaluatedDetectables) {
                    DetectableDefinition detectable = evaluated.getDetectableDefinition();
                    if (!evaluated.wasExtractionSuccessful()) {
                        reportAttempted(evaluated, detectable)
                            .ifPresent(attemptedDetectables::add);
                    } else {
                        extracted = evaluated;
                    }
                }
                if (extracted != null) {
                    ExtractedDetectableReport extractedDetectableReport = ExtractedDetectableReport.extracted(
                        extracted.getDetectableDefinition(),
                        extracted.getApplicable(),
                        extracted.getExtractable(),
                        extracted.getExtraction(),
                        extracted.getExtractionEnvironment().orElse(null) //TODO(detectors): why is this optional but not extraction?
                    );
                    extractedDetectors.add(EvaluatedDetectorRuleReport.extracted(ruleEvaluation.getRule(), notFoundEntryPoints, attemptedDetectables, extractedDetectableReport));
                } else {
                    notExtractedDetectors.add(EvaluatedDetectorRuleReport.notExtracted(ruleEvaluation.getRule(), notFoundEntryPoints, attemptedDetectables));
                }
            });

            reports.add(new DetectorDirectoryReport(evaluation.getDirectory(), evaluation.getDepth(), notFoundDetectors, extractedDetectors, notExtractedDetectors));
        });

        return reports;
    }

    private Optional<AttemptedDetectableReport> reportAttempted(DetectableEvaluationResult evaluated, DetectableDefinition detectable) {
        if (!evaluated.getApplicable().getPassed()) {
            return Optional.of(AttemptedDetectableReport.notApplicable(detectable, evaluated.getApplicable()));
        } else if (evaluated.getExtractable() != null && !evaluated.getExtractable().getPassed()) {
            return Optional.of(AttemptedDetectableReport.notExtractable(detectable, evaluated.getApplicable(), evaluated.getExtractable()));
        } else if (evaluated.getExtraction() != null) {
            return Optional.of(AttemptedDetectableReport.notExtracted(
                detectable,
                evaluated.getApplicable(),
                evaluated.getExtractable(),
                evaluated.getExtraction()
            ));
        } else {
            logger.error("Something has gone wrong in the detector system, please contact support.");
        }
        return Optional.empty();
    }

    private List<NotFoundDetectorRuleReport> reportNotFound(DetectorEvaluation evaluation) {
        return evaluation.getNotFoundDetectorSearches().stream()
            .map(this::reportNotFoundDetector)
            .collect(Collectors.toList());
    }

    private NotFoundDetectorRuleReport reportNotFoundDetector(DetectorRuleNotFoundResult notFoundResult) {
        DetectorSearchResult search = notFoundResult.getSearchResult();
        if (search.getNotSearchableResult().isPresent()) {
            return NotFoundDetectorRuleReport.notSearchable(notFoundResult.getRule(), search.getNotSearchableResult().get());
        } else {
            return NotFoundDetectorRuleReport.noApplicableEntryPoint(notFoundResult.getRule(), search.getNotFoundEntryPoints());
        }
    }
}
