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

import com.blackducksoftware.integration.hub.detect.diagnostic.DiagnosticsManager;
import com.blackducksoftware.integration.hub.detect.extraction.StrategyEvaluation;

@Component
public class PreparationSummaryReporter {
    private final Logger logger = LoggerFactory.getLogger(PreparationSummaryReporter.class);

    @Autowired
    public DiagnosticsManager diagnosticsManager;

    public void print(final List<StrategyEvaluation> results) {
        final Map<File, List<StrategyEvaluation>> byDirectory = new HashMap<>();
        for (final StrategyEvaluation result : results) {
            if (result.context == null || result.environment.getDirectory() == null) {
                info("WUT");
            }
            final File directory = result.environment.getDirectory();
            if (!byDirectory.containsKey(directory)) {
                byDirectory.put(directory, new ArrayList<>());
            }
            byDirectory.get(directory).add(result);
        }

        printDirectories(byDirectory);

    }

    private void printDirectories(final Map<File, List<StrategyEvaluation>> byDirectory) {
        logger.info("");
        logger.info("");
        info(ReportConstants.HEADING);
        info("Preparation for extraction");
        info(ReportConstants.HEADING);
        for (final File file : byDirectory.keySet()) {
            final List<StrategyEvaluation> results = byDirectory.get(file);

            final List<String> ready = new ArrayList<>();
            final List<String> failed = new ArrayList<>();

            for (final StrategyEvaluation result : results) {
                final String strategyName = result.strategy.getBomToolType() + " - " + result.strategy.getName();
                if (result.isApplicable()) {
                    if (result.extractable.getPassed()) {
                        ready.add(strategyName);
                    } else {
                        failed.add("FAILED: " + strategyName + " - " + result.extractable.toDescription());
                    }
                }
            }
            if (ready.size() > 0 || failed.size() > 0) {
                info(file.getAbsolutePath());
                if (ready.size() > 0) {
                    info("\t READY: " + ready.stream().sorted().collect(Collectors.joining(", ")));
                }
                if (failed.size() > 0) {
                    failed.stream().sorted().forEach(it -> info("\t" + it));
                }

            }
        }
        info(ReportConstants.HEADING);
        logger.info("");
        logger.info("");
    }

    private void info(final String line) {
        logger.info(line);
        diagnosticsManager.printToPreparationReport(line);
    }

}
