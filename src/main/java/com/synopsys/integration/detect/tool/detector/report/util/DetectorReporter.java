package com.synopsys.integration.detect.tool.detector.report.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.tool.detector.report.DetectorDirectoryReport;
import com.synopsys.integration.detect.tool.detector.report.detectable.AttemptedDetectableReport;
import com.synopsys.integration.detect.tool.detector.report.detectable.ExtractedDetectableReport;
import com.synopsys.integration.detect.tool.detector.report.rule.EvaluatedDetectorRuleReport;
import com.synopsys.integration.detect.tool.detector.report.rule.ExtractedDetectorRuleReport;
import com.synopsys.integration.detect.tool.detector.report.rule.NotFoundDetectorRuleReport;
import com.synopsys.integration.detector.accuracy.detectable.DetectableEvaluationResult;
import com.synopsys.integration.detector.accuracy.directory.DirectoryEvaluation;
import com.synopsys.integration.detector.accuracy.entrypoint.DetectorRuleEvaluation;
import com.synopsys.integration.detector.accuracy.entrypoint.EntryPointFoundResult;
import com.synopsys.integration.detector.base.DetectorEvaluationUtil;
import com.synopsys.integration.detector.rule.DetectableDefinition;

public class DetectorReporter {
    private static final String DETECTOR_ERROR_MESSAGE = "Something has gone wrong in the detector system, please contact support.";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<DetectorDirectoryReport> generateReport(DirectoryEvaluation rootDirectoryEvaluation) {
        List<DetectorDirectoryReport> reports = new ArrayList<>();
        List<DirectoryEvaluation> directoryEvaluations = DetectorEvaluationUtil.asFlatList(rootDirectoryEvaluation);

        directoryEvaluations.forEach(directoryEvaluation -> {
            List<NotFoundDetectorRuleReport> notFoundDetectors = new ArrayList<>();
            List<ExtractedDetectorRuleReport> extractedDetectors = new ArrayList<>();
            List<EvaluatedDetectorRuleReport> notExtractedDetectors = new ArrayList<>();

            directoryEvaluation.getEvaluations().forEach(detectorRuleEvaluation -> {
                if (!detectorRuleEvaluation.wasFound()) {
                    notFoundDetectors.add(generateNotFoundReport(detectorRuleEvaluation));
                } else if (detectorRuleEvaluation.getFoundEntryPoint().isPresent()) { //just to check :)
                    addFoundReport(
                        directoryEvaluation.getDepth(),
                        detectorRuleEvaluation,
                        detectorRuleEvaluation.getFoundEntryPoint().get(),
                        extractedDetectors,
                        notExtractedDetectors
                    );
                } else {
                    logger.error(DETECTOR_ERROR_MESSAGE);
                }
            });
            reports.add(new DetectorDirectoryReport(
                directoryEvaluation.getDirectory(),
                directoryEvaluation.getDepth(),
                notFoundDetectors,
                extractedDetectors,
                notExtractedDetectors
            ));
        });

        return reports;
    }

    private void addFoundReport(
        int depth,
        DetectorRuleEvaluation ruleEvaluation,
        EntryPointFoundResult foundEntryPoint,
        List<ExtractedDetectorRuleReport> extractedDetectors,
        List<EvaluatedDetectorRuleReport> notExtractedDetectors
    ) {

        List<AttemptedDetectableReport> notFoundEntryPoints = new ArrayList<>();
        ruleEvaluation.getNotFoundEntryPoints().forEach(notFoundEntry -> {
            if (!notFoundEntry.getSearchResult().getPassed()) {
                //searchable failure
                notFoundEntryPoints.add(AttemptedDetectableReport.notSearchable(notFoundEntry.getEntryPoint().getPrimary(), notFoundEntry.getSearchResult()));
            } else if (notFoundEntry.getApplicableResult().isPresent()) {
                //applicable failure
                notFoundEntryPoints.add(AttemptedDetectableReport.notApplicable(notFoundEntry.getEntryPoint().getPrimary(), notFoundEntry.getApplicableResult().get()));
            } else {
                logger.error(DETECTOR_ERROR_MESSAGE);
            }
        });

        List<AttemptedDetectableReport> attemptedDetectables = new ArrayList<>();
        List<DetectableEvaluationResult> evaluatedDetectables = foundEntryPoint.getEntryPointEvaluation().getEvaluatedDetectables();
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
            extractedDetectors.add(EvaluatedDetectorRuleReport.extracted(ruleEvaluation.getRule(), depth, notFoundEntryPoints, attemptedDetectables, extractedDetectableReport));
        } else {
            notExtractedDetectors.add(EvaluatedDetectorRuleReport.notExtracted(ruleEvaluation.getRule(), depth, notFoundEntryPoints, attemptedDetectables));
        }
    }

    private NotFoundDetectorRuleReport generateNotFoundReport(DetectorRuleEvaluation detectorRuleEvaluation) {
        List<String> reasons = new ArrayList<>();
        detectorRuleEvaluation.getNotFoundEntryPoints().forEach(result -> {
            if (!result.getSearchResult().getPassed()) {//if not searchable, reason is the searchable, otherwise reason is applicable if present.
                reasons.add(result.getEntryPoint().getPrimary().getName() + ": " + result.getSearchResult().getDescription());
            }
            result.getApplicableResult().ifPresent(applicable -> reasons.add(result.getEntryPoint().getPrimary().getName() + ": " + applicable.toDescription()));
        });
        return new NotFoundDetectorRuleReport(detectorRuleEvaluation.getRule(), reasons);
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
            logger.error(DETECTOR_ERROR_MESSAGE);
        }
        return Optional.empty();
    }
}
