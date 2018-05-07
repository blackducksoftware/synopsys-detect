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
        final List<Info> infos = new ArrayList<>();
        for (final File file : byDirectory.keySet()) {
            final List<StrategyFindResult> results = byDirectory.get(file);
            int codelocations = 0;
            int applied = 0;
            int demanded = 0;
            int extracted = 0;
            String success = "";
            String exception = "";
            String failed = "";
            for (final StrategyFindResult result : results) {
                final String strategyName = result.strategy.getBomToolType() + " - " + result.strategy.getName();
                if (result.type == FindType.APPLIES) {
                    applied++;
                }
                if (result.type == FindType.APPLIES && result.evaluation.areNeedsMet()) {
                    demanded++;
                }
                if (result.type == FindType.APPLIES && result.evaluation.areNeedsMet() && result.evaluation.areDemandsMet()) {
                    extracted++;
                }
                if (result.type == FindType.APPLIES && result.evaluation.areNeedsMet() && result.evaluation.areDemandsMet()) {
                    codelocations += result.evaluation.extraction.codeLocations.size();
                    if (result.evaluation.extraction.result == ExtractionResult.Success) {
                        if (success.length() != 0) {
                            success += ", ";
                        }
                        success += strategyName;
                    } else if (result.evaluation.extraction.result == ExtractionResult.Failure) {
                        if (failed.length() != 0) {
                            failed += ", ";
                        }
                        failed += strategyName;
                    } else if (result.evaluation.extraction.result == ExtractionResult.Exception) {
                        if (exception.length() != 0) {
                            exception += ", ";
                        }
                        exception += strategyName;
                    }
                }
            }
            final Info info = new Info();
            info.directory = file.getAbsolutePath();
            info.codeLocations = "\t Code Locations: " + Integer.toString(codelocations);
            info.success = success;
            info.failed = failed;
            info.exception = exception;
            info.applied = applied;
            info.demanded = demanded;
            info.extracted = extracted;
            infos.add(info);
        }
        final List<Info> stream = infos.stream().sorted((o1, o2) -> o1.directory.compareTo(o2.directory)).collect(Collectors.toList());

        logger.info("Extraction results:");
        stream.stream().sorted((o1, o2) -> o1.codeLocations.compareTo(o2.codeLocations)).forEach(it -> {
            if (it.extracted > 0) {
                logger.info(it.directory);
                logger.info(it.codeLocations);
                if (!it.success.equals("")) {
                    logger.info("\t Success: " + it.success);
                }
                if (!it.failed.equals("")) {
                    logger.info("\t Failure: " + it.failed);
                }
                if (!it.exception.equals("")) {
                    logger.info("\t Exception: " + it.exception);
                }
            }
        });

        printSeperator();

        logger.info("Not applicable:");

        stream.stream().forEach(it -> {
            if (it.applied == 0) {
                logger.info("\t" + it.directory);
            }
        });

        printSeperator();

    }

    private class Info {
        public String codeLocations;
        public String directory;
        public String success;
        public String failed;
        public String exception;
        public int applied;
        public int demanded;
        public int extracted;
    }

    private void printSeperator() {
        logger.info("------------------------------------------------------------------------------------------------------");
    }
}