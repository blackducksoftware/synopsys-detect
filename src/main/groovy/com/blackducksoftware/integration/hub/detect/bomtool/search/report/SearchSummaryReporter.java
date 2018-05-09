package com.blackducksoftware.integration.hub.detect.bomtool.search.report;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.bomtool.search.StrategyFindResult;
import com.blackducksoftware.integration.hub.detect.bomtool.search.StrategyFindResult.FindType;
import com.blackducksoftware.integration.hub.detect.bomtool.search.StrategyFindResult.Reason;
import com.blackducksoftware.integration.hub.detect.diagnostic.DiagnosticsManager;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.Requirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation.EvaluationResult;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;

@Component
public class SearchSummaryReporter {
    private final Logger logger = LoggerFactory.getLogger(SearchSummaryReporter.class);

    @Autowired
    public DiagnosticsManager diagnosticsManager;

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

        printDirectoriesInfo(byDirectory);
        printDirectoriesDebug(byDirectory);

    }

    private void printDirectoriesInfo(final Map<File, List<StrategyFindResult>> byDirectory) {

        logger.info("");
        logger.info("");
        logger.info(ReportConstants.HEADING);
        logger.info("Search results");
        logger.info(ReportConstants.HEADING);
        for (final File file : byDirectory.keySet()) {
            final List<StrategyFindResult> results = byDirectory.get(file);

            final List<String> applied = new ArrayList<>();

            for (final StrategyFindResult result : results) {
                final String strategyName = result.strategy.getBomToolType() + " - " + result.strategy.getName();
                if (result.type == FindType.APPLIES) {
                    applied.add(strategyName);
                }
            }
            if (applied.size() > 0) {

                logger.info(file.getAbsolutePath());
                logger.info("\tAPPLIES: " + applied.stream().sorted().collect(Collectors.joining(", ")));

            }
        }
        logger.info(ReportConstants.HEADING);
        logger.info("");
        logger.info("");
    }

    private void printDirectoriesDebug(final Map<File, List<StrategyFindResult>> byDirectory) {
        for (final File file : byDirectory.keySet()) {
            final List<StrategyFindResult> results = byDirectory.get(file);

            final List<String> toPrint = new ArrayList<>();

            for (final StrategyFindResult result : results) {
                final String strategyName = result.strategy.getBomToolType() + " - " + result.strategy.getName();
                if (result.type == FindType.APPLIES) {
                    toPrint.add("      APPLIED: " + strategyName);
                } else {
                    if (result.reason == Reason.YIELDED) {
                        toPrint.add("DID NOT APPLY: " + strategyName + " - YIELDED - " + summarizeYielded(result));
                    } else if (result.reason == Reason.NEEDS_NOT_MET) {
                        toPrint.add("DID NOT APPLY: " + strategyName + " - NEEDS NOT MET - " + summarizeFailed(result));
                    } else if (result.reason == Reason.MAX_DEPTH_EXCEEDED) {
                        toPrint.add("DID NOT APPLY: " + strategyName + " - MAX DEPTH EXCEEDED - " + summarizeDepth(result));
                    } else if (result.reason == Reason.NOT_NESTABLE) {
                        toPrint.add("DID NOT APPLY: " + strategyName + " - CAN NOT NEST - " + summarizeNested(result));
                    }
                }
            }
            if (toPrint.size() > 0) {

                debug(ReportConstants.HEADING);
                debug("Detailed search results for directory");
                debug(file.getAbsolutePath());
                debug(ReportConstants.HEADING);
                toPrint.stream().sorted().forEach(it -> debug(it));
                debug(ReportConstants.HEADING);
            }
        }
    }

    private void debug(final String line) {
        logger.debug(line);
        diagnosticsManager.printToSearchReport(line);
    }

    private String summarizeDepth(final StrategyFindResult result) {
        return "At depth of " + result.depth + " but max depth is " + result.strategy.getSearchOptions().getMaxDepth();
    }

    private String summarizeNested(final StrategyFindResult result) {
        return "and " + result.nested.stream().map(it -> it.getName()).collect(Collectors.joining(",")) + " already applied in a parent directory.";
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
}
