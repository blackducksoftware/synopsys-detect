package com.blackducksoftware.integration.hub.detect.bomtool.search.report;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.bomtool.search.StrategyFindResult;
import com.blackducksoftware.integration.hub.detect.bomtool.search.StrategyFindResult.FindType;
import com.blackducksoftware.integration.hub.detect.diagnostic.DiagnosticsManager;
import com.blackducksoftware.integration.hub.detect.extraction.Applicable.ApplicableResult;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable.ExtractableResult;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;

@Component
public class ExtractionSummaryReporter {
    private final Logger logger = LoggerFactory.getLogger(PreparationSummaryReporter.class);

    @Autowired
    public DiagnosticsManager diagnosticsManager;

    public void print(final List<StrategyFindResult> results) {
        final Map<File, List<StrategyFindResult>> byDirectory = new HashMap<>();
        for (final StrategyFindResult result : results) {
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

        byDirectory.keySet().stream().forEach(file -> {
            final List<StrategyFindResult> results = byDirectory.get(file);
            int codelocations = 0;
            final List<String> codelocationnames = new ArrayList<>();
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
                if (result.type == FindType.APPLIES && result.evaluation.applicable.result == ApplicableResult.APPLIES) {
                    demanded++;
                }
                if (result.type == FindType.APPLIES  && result.evaluation.applicable.result == ApplicableResult.APPLIES && result.evaluation.extractable.result == ExtractableResult.EXTRACTABLE) {
                    extracted++;
                }
                if (result.type == FindType.APPLIES  && result.evaluation.applicable.result == ApplicableResult.APPLIES && result.evaluation.extractable.result == ExtractableResult.EXTRACTABLE) {
                    codelocations += result.evaluation.extraction.codeLocations.size();

                    result.evaluation.extraction.codeLocations.stream().forEach(it -> {
                        final List<String> pieces = Arrays.asList(it.getBomToolProjectExternalId().getExternalIdPieces());
                        final String name = pieces.stream().collect(Collectors.joining("\\"));
                        codelocationnames.add(name);
                    });
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
            info.codeLocations = "\tCode Locations: " + Integer.toString(codelocations);
            info.codeLocationNames = codelocationnames;
            info.success = success;
            info.failed = failed;
            info.exception = exception;
            info.applied = applied;
            info.demanded = demanded;
            info.extracted = extracted;
            infos.add(info);
        });
        final List<Info> stream = infos.stream().sorted((o1, o2) -> {
            final String[] pieces1 = o1.directory.split(Pattern.quote(File.separator));
            final String[] pieces2 = o2.directory.split(Pattern.quote(File.separator));
            final int min = Math.min(pieces1.length, pieces2.length);
            for (int i = 0; i < min; i++) {
                final int compared = pieces1[i].compareTo(pieces2[i]);
                if (compared != 0){
                    return compared;
                }
            }
            return Integer.compare(pieces1.length, pieces2.length);
        }).collect(Collectors.toList());
        logger.info("");
        logger.info("");
        info(ReportConstants.HEADING);
        info("Extraction results:");
        info(ReportConstants.HEADING);
        stream.stream().forEach(it -> {
            if (it.extracted > 0) {
                info(it.directory);
                info(it.codeLocations);
                it.codeLocationNames.stream().forEach(name -> info("\t\t" + name));
                if (!it.success.equals("")) {
                    info("\tSuccess: " + it.success);
                }
                if (!it.failed.equals("")) {
                    info("\tFailure: " + it.failed);
                }
                if (!it.exception.equals("")) {
                    info("\tException: " + it.exception);
                }
            }
        });
        info(ReportConstants.HEADING);
        logger.info("");
        logger.info("");
    }

    private void info(final String line) {
        logger.info(line);
        diagnosticsManager.printToExtractionReport(line);
    }

    private class Info {
        public String codeLocations;
        public List<String> codeLocationNames;
        public String directory;
        public String success;
        public String failed;
        public String exception;
        public int applied;
        public int demanded;
        public int extracted;
    }

}