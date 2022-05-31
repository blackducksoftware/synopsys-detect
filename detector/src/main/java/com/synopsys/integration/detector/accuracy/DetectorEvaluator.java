package com.synopsys.integration.detector.accuracy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.finder.DirectoryFindResult;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;
import com.synopsys.integration.detector.rule.EntryPoint;

public class DetectorEvaluator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectorSearch detectorSearch;
    private final DetectorExtract detectorExtract;
    private final DetectorEvaluationOptions evaluationOptions;
    private final Function<DetectorType, ExtractionEnvironment> extractionEnvironmentSupplier;

    public DetectorEvaluator(
        DetectorSearch detectorSearch,
        DetectorExtract detectorExtract,
        DetectorEvaluationOptions evaluationOptions,
        Function<DetectorType, ExtractionEnvironment> extractionEnvironmentSupplier
    ) {
        this.detectorSearch = detectorSearch;
        this.detectorExtract = detectorExtract;
        this.evaluationOptions = evaluationOptions;
        this.extractionEnvironmentSupplier = extractionEnvironmentSupplier;
    }

    public DetectorEvaluation evaluate(DirectoryFindResult rootDirectory, DetectorRuleSet rules) {
        logger.info("Evaluating detectors. This may take a while.");
        return evaluate(rootDirectory, rules, new HashSet<>());
    }

    protected DetectorEvaluation evaluate(DirectoryFindResult findResult, DetectorRuleSet rules, Set<DetectorRule> appliedInParent) {
        logger.trace("Determining applicable detectors on the directory: {}", findResult.getDirectory());

        File directory = findResult.getDirectory();
        Set<DetectorRule> appliedSoFar = new HashSet<>();
        List<DetectorRuleEvaluation> detectorRuleEvaluations = new LinkedList<>();

        for (DetectorRule rule : rules.getDetectorRules()) {
            SearchEnvironment searchEnvironment = new SearchEnvironment(
                findResult.getDepthFromRoot(),
                evaluationOptions.getDetectorFilter(),
                evaluationOptions.isForceNested(),
                evaluationOptions.isFollowSymLinks(),
                appliedInParent,
                appliedSoFar
            );

            DetectableEnvironment detectableEnvironment = new DetectableEnvironment(directory);
            DetectorSearchResult searchResult = detectorSearch.evaluate(searchEnvironment, detectableEnvironment, rule);

            logger.trace("Evaluating detector: {}", rule.getDetectorType());

            if (searchResult.wasFound() && searchResult.getEntryPoint().isPresent()) {
                logger.trace("Found detector, will continue evaluating."); //TODO: May need a log here.

                EntryPoint entryPoint = searchResult.getEntryPoint().get();
                EntryPointEvaluation entryPointEvaluation = detectorExtract.extract(
                    entryPoint,
                    detectableEnvironment,
                    () -> extractionEnvironmentSupplier.apply(rule.getDetectorType())
                );

                DetectorRuleEvaluation detectorRuleEvaluation = new DetectorRuleEvaluation(detectableEnvironment, rule, entryPoint, entryPointEvaluation);
                detectorRuleEvaluations.add(detectorRuleEvaluation);

                logger.trace("Extracted: {}", rule.getDetectorType());
                appliedSoFar.add(rule);
            } else {
                logger.trace("Not searchable or not applicable: {}", searchResult.getMessage());
            }

        }

        if (!appliedSoFar.isEmpty()) {
            logger.debug("Found ({}) applicable detectors in: {}", appliedSoFar.size(), directory);
        }

        Set<DetectorRule> nextAppliedInParent = new HashSet<>();
        nextAppliedInParent.addAll(appliedInParent);
        nextAppliedInParent.addAll(appliedSoFar);

        List<DetectorEvaluation> children = new ArrayList<>();
        for (DirectoryFindResult subdirectory : findResult.getChildren()) {
            DetectorEvaluation child = evaluate(subdirectory, rules, nextAppliedInParent);
            children.add(child);
        }

        return new DetectorEvaluation(directory, findResult.getDepthFromRoot(), detectorRuleEvaluations, children);
    }
}

