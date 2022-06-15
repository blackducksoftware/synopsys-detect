package com.synopsys.integration.detector.accuracy.directory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detector.accuracy.entrypoint.DetectorRuleEvaluation;
import com.synopsys.integration.detector.accuracy.entrypoint.DetectorRuleEvaluator;
import com.synopsys.integration.detector.accuracy.entrypoint.EntryPointFoundResult;
import com.synopsys.integration.detector.accuracy.search.SearchEnvironment;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.finder.DirectoryFindResult;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

public class DirectoryEvaluator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectorRuleEvaluator detectorRuleEvaluator;
    private final Function<DetectorType, ExtractionEnvironment> extractionEnvironmentSupplier;

    public DirectoryEvaluator(
        DetectorRuleEvaluator detectorRuleEvaluator,
        Function<DetectorType, ExtractionEnvironment> extractionEnvironmentSupplier
    ) {
        this.detectorRuleEvaluator = detectorRuleEvaluator;
        this.extractionEnvironmentSupplier = extractionEnvironmentSupplier;
    }

    public DirectoryEvaluation evaluate(DirectoryFindResult rootDirectory, DetectorRuleSet rules) {
        logger.info("Evaluating detectors. This may take a while.");
        return evaluate(rootDirectory, rules, new HashSet<>());
    }

    protected DirectoryEvaluation evaluate(DirectoryFindResult findResult, DetectorRuleSet rules, Set<DetectorRule> appliedInParent) {
        logger.trace("Determining applicable detectors on the directory: {}", findResult.getDirectory());

        File directory = findResult.getDirectory();
        Set<DetectorRule> appliedSoFar = new HashSet<>();
        List<DetectorRuleEvaluation> evaluations = new LinkedList<>();

        for (DetectorRule rule : rules.getDetectorRules()) {
            SearchEnvironment searchEnvironment = new SearchEnvironment(findResult.getDepthFromRoot(), appliedSoFar, appliedInParent);
            DetectorRuleEvaluation detectorRuleEvaluation = detectorRuleEvaluator.evaluate(
                directory,
                searchEnvironment,
                rule,
                () -> extractionEnvironmentSupplier.apply(rule.getDetectorType())
            );
            if (detectorRuleEvaluation.wasFound() && detectorRuleEvaluation.getFoundEntryPoint().isPresent()) { //should this capture only success?
                EntryPointFoundResult foundEntryPoint = detectorRuleEvaluation.getFoundEntryPoint().get();
                // foundEntryPoint // CAPTURE the details //ie fill appliedSoFar

            }
            evaluations.add(detectorRuleEvaluation);
        }

        if (!appliedSoFar.isEmpty()) {
            logger.debug("Found ({}) applicable detectors in: {}", appliedSoFar.size(), directory);
        }

        Set<DetectorRule> nextAppliedInParent = new HashSet<>();
        nextAppliedInParent.addAll(appliedInParent);
        nextAppliedInParent.addAll(appliedSoFar);

        List<DirectoryEvaluation> children = new ArrayList<>();
        for (DirectoryFindResult subdirectory : findResult.getChildren()) {
            DirectoryEvaluation child = evaluate(subdirectory, rules, nextAppliedInParent);
            children.add(child);
        }

        return new DirectoryEvaluation(directory, findResult.getDepthFromRoot(), evaluations, children);
    }
}

