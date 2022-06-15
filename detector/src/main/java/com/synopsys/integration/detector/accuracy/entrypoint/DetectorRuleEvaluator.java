package com.synopsys.integration.detector.accuracy.detector;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detector.accuracy.DetectorRuleEvaluation;
import com.synopsys.integration.detector.accuracy.DetectorRuleNotFoundResult;
import com.synopsys.integration.detector.accuracy.EntryPointEvaluation;
import com.synopsys.integration.detector.accuracy.search.SearchEnvironment;
import com.synopsys.integration.detector.accuracy.search.SearchEvaluator;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.EntryPoint;

public class DetectorEntryPointsEvaluator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SearchEvaluator searchEvaluator;

    public DetectorEntryPointsEvaluator(SearchEvaluator searchEvaluator) {this.searchEvaluator = searchEvaluator;}

    public void evaluate(File directory, SearchEnvironment searchEnvironment, DetectorRule detectorRule) {

        if (searchResult.getFoundEntryPoint().isPresent()) {
            logger.trace("Found detector, will continue evaluating.");

            EntryPoint entryPoint = searchResult.getFoundEntryPoint().get().getEntryPoint();
            EntryPointEvaluation entryPointEvaluation = detectorExtract.extract(
                entryPoint,
                detectableEnvironment,
                () -> extractionEnvironmentSupplier.apply(rule.getDetectorType())
            );

            DetectorRuleEvaluation detectorRuleEvaluation = new DetectorRuleEvaluation(
                rule,
                detectableEnvironment,
                searchResult.getNotFoundEntryPoints(),
                entryPointEvaluation
            );
            foundRules.add(detectorRuleEvaluation);

            logger.trace("Extracted: {}", rule.getDetectorType());
            appliedSoFar.add(rule);
        } else {
            notFoundRules.add(new DetectorRuleNotFoundResult(rule, searchResult));
            if (searchResult.getNotSearchableResult().isPresent()) {
                logger.trace("Not searchable or none found: {}", searchResult.getNotSearchableResult().get().getDescription());
            } else {
                logger.trace("Not found but was searchable.");
            }
        }
    }
}
