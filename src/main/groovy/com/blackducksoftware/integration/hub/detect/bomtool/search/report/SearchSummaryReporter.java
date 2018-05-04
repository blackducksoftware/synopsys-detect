package com.blackducksoftware.integration.hub.detect.bomtool.search.report;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.bomtool.search.StrategyFindResult;
import com.blackducksoftware.integration.hub.detect.bomtool.search.StrategyFindResult.FindType;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.Requirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation.EvaluationResult;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;

@Component
public class SearchSummaryReporter {
    private final Logger logger = LoggerFactory.getLogger(SearchSummaryReporter.class);

    public void print(final List<StrategyFindResult> results) {
        final Map<File, List<StrategyFindResult>> byDirectory = new HashMap<>();
        for (final StrategyFindResult result : results) {
            if (result.context == null || result.context.getDirectory() == null) {
                logger.info("WUT");
            }
            final File directory = result.context.getDirectory();
            if (!byDirectory.containsKey(directory)) {
                byDirectory.put(directory, new ArrayList<>());
            }
            byDirectory.get(directory).add(result);
        }

        printDirectories(byDirectory);

    }

    private void printDirectories(final Map<File, List<StrategyFindResult>> byDirectory) {
        for (final File file : byDirectory.keySet()) {
            final List<StrategyFindResult> results = byDirectory.get(file);

            printSeperator();
            logger.info("Search results for directory");
            logger.info(file.getAbsolutePath());
            printSeperator();
            final List<String> toPrint = new ArrayList<>();
            for (final StrategyFindResult result : results) {
                final String strategyName = result.strategy.getBomToolType() + " - " + result.strategy.getName();
                if (result.type == FindType.APPLIES) {
                    toPrint.add("APPLIES: " + strategyName);
                } else if (result.type == FindType.YIELDED) {
                    toPrint.add("YIELDED: " + strategyName + " - " + summarizeYielded(result));
                } else if (result.type == FindType.NEEDS_NOT_MET) {
                    toPrint.add("SKIPPED: " + strategyName + " - " + summarizeFailed(result));
                }
            }
            toPrint.stream().sorted().forEach(it -> logger.info(it));
            printSeperator();
        }
    }

    private String summarizeYielded(final StrategyFindResult result) {

        final List<Strategy> yieldedTo = result.evaluation.getYieldedTo();

        if (yieldedTo.size() > 0) {
            if (result.evaluation.getYieldedTo().size() > 0) {
                final String yielded = yieldedTo.stream().map(it -> it.getName()).collect(Collectors.joining(","));
                return "because " + yielded + " already applied.";
            }
        }
        return "Unkown";
    }

    private String summarizeFailed(final StrategyFindResult result) {
        for (final Requirement req : result.evaluation.needEvaluationMap.keySet()){
            final RequirementEvaluation eval = result.evaluation.needEvaluationMap.get(req);
            if (eval.result == EvaluationResult.Failed) {
                return eval.description;
            }
        }
        return "Unkown";
    }

    private void printSeperator() {
        logger.info("------------------------------------------------------------------------------------------------------");
    }
}
