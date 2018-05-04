package com.blackducksoftware.integration.hub.detect.bomtool.search.report;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.bomtool.search.StrategyFindResult;
import com.blackducksoftware.integration.hub.detect.bomtool.search.StrategyFindResult.FindType;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;

@Component
public class ExtractionSummaryReporter {
    private final Logger logger = LoggerFactory.getLogger(PreparationSummaryReporter.class);

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
            logger.info("Extraction results");
            logger.info(file.getAbsolutePath());
            printSeperator();
            final List<String> toPrint = new ArrayList<>();
            for (final StrategyFindResult result : results) {
                final String strategyName = result.strategy.getBomToolType() + " - " + result.strategy.getName();
                if (result.type == FindType.APPLIES && result.evaluation.areNeedsMet() && result.evaluation.areDemandsMet()) {
                    if (result.evaluation.extraction.result == ExtractionResult.Success) {
                        toPrint.add("SUCCESS: " + strategyName + " - " + result.evaluation.extraction.codeLocations.size() + " code locations found.");
                    } else if (result.evaluation.extraction.result == ExtractionResult.Failure) {
                        toPrint.add("FAILED: " + strategyName + " - " + result.evaluation.extraction.description);
                    } else if (result.evaluation.extraction.result == ExtractionResult.Exception) {
                        toPrint.add("EXCEPTION: " + strategyName + " - " + result.evaluation.extraction.error.getMessage());

                    }
                }
            }
            toPrint.stream().sorted().forEach(it -> logger.info(it));
            printSeperator();
        }
    }

    private void printSeperator() {
        logger.info("------------------------------------------------------------------------------------------------------");
    }
}