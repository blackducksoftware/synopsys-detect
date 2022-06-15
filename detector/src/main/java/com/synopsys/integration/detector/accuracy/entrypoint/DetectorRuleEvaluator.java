package com.synopsys.integration.detector.accuracy.entrypoint;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detector.accuracy.detectable.DetectableEvaluationResult;
import com.synopsys.integration.detector.accuracy.detectable.DetectableEvaluator;
import com.synopsys.integration.detector.accuracy.search.SearchEnvironment;
import com.synopsys.integration.detector.accuracy.search.SearchEvaluator;
import com.synopsys.integration.detector.result.DetectorResult;
import com.synopsys.integration.detector.rule.DetectableDefinition;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.EntryPoint;

public class DetectorRuleEvaluator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SearchEvaluator searchEvaluator;
    private final DetectableEvaluator detectableEvaluator;

    public DetectorRuleEvaluator(SearchEvaluator searchEvaluator, DetectableEvaluator detectableEvaluator) {
        this.searchEvaluator = searchEvaluator;
        this.detectableEvaluator = detectableEvaluator;
    }

    public DetectorRuleEvaluation evaluate(
        File directory,
        SearchEnvironment searchEnvironment,
        DetectorRule detectorRule,
        Supplier<ExtractionEnvironment> extractionEnvironmentSupplier
    ) {
        List<EntryPointNotFoundResult> notFoundEntryPoints = new ArrayList<>();
        EntryPointFoundResult foundEntryPoint = null;
        DetectableEnvironment detectableEnvironment = new DetectableEnvironment(directory);
        for (EntryPoint entryPoint : detectorRule.getEntryPoints()) {
            DetectorResult searchResult = searchEvaluator.evaluateSearchable(
                detectorRule.getDetectorType(),
                entryPoint.getSearchRule(),
                searchEnvironment
            );
            if (!searchResult.getPassed()) {
                notFoundEntryPoints.add(EntryPointNotFoundResult.notSearchable(entryPoint, searchResult));
                continue;
            }

            Detectable primaryDetectable = entryPoint.getPrimary().getDetectableCreatable().createDetectable(detectableEnvironment);
            DetectableResult applicable = primaryDetectable.applicable();
            if (!applicable.getPassed()) {
                notFoundEntryPoints.add(EntryPointNotFoundResult.notApplicable(entryPoint, searchResult, applicable));
                continue;
            }

            EntryPointEvaluation entryPointEvaluation = extract(entryPoint, detectableEnvironment, extractionEnvironmentSupplier);
            foundEntryPoint = EntryPointFoundResult.evaluated(entryPoint, searchResult, applicable, entryPointEvaluation);
            break; //Either way, we have found an entry point and extracted. We are done.
        }
        return new DetectorRuleEvaluation(detectorRule, detectableEnvironment, notFoundEntryPoints, foundEntryPoint);
    }

    public EntryPointEvaluation extract(EntryPoint entryPoint, DetectableEnvironment detectableEnvironment, Supplier<ExtractionEnvironment> extractionEnvironmentSupplier) {
        List<DetectableDefinition> toCascade = entryPoint.allDetectables();
        List<DetectableEvaluationResult> evaluated = new ArrayList<>();
        for (DetectableDefinition detectable : toCascade) {
            DetectableEvaluationResult detectableEvaluationResult = detectableEvaluator.evaluate(detectable, detectableEnvironment, extractionEnvironmentSupplier);
            evaluated.add(detectableEvaluationResult);
            if (detectableEvaluationResult.wasExtractionSuccessful()) {
                break;
            }
        }
        return new EntryPointEvaluation(entryPoint, evaluated);
    }
}
