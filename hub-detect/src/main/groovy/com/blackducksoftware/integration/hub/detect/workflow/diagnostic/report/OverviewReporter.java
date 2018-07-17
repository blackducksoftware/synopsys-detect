package com.blackducksoftware.integration.hub.detect.workflow.diagnostic.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ReportConstants;

public class OverviewReporter {

    public void writeReport(final DiagnosticReportWriter writer, final List<BomToolEvaluation> results) {
        final Map<File, List<BomToolEvaluation>> byDirectory = results.stream()
                .collect(Collectors.groupingBy(item -> item.getEnvironment().getDirectory()));

        printDirectories(writer, byDirectory);

    }

    private void printDirectories(final DiagnosticReportWriter writer, final Map<File, List<BomToolEvaluation>> byDirectory) {
        writer.writeSeperator();
        for (final File file : byDirectory.keySet()) {
            final List<BomToolEvaluation> results = byDirectory.get(file);

            final List<BomToolEvaluation> applicable = new ArrayList<>();
            final List<BomToolEvaluation> extractable = new ArrayList<>();
            final List<BomToolEvaluation> extractionSuccess = new ArrayList<>();
            final List<BomToolEvaluation> extractionFailure = new ArrayList<>();

            for (final BomToolEvaluation result : results) {
                if (result.isApplicable()) {
                    applicable.add(result);
                    if (result.isExtractable()) {
                        extractable.add(result);
                        if (result.wasExtractionSuccessful()) {
                            extractionSuccess.add(result);
                        } else {
                            extractionFailure.add(result);
                        }
                    }
                }
            }
            if (applicable.size() > 0) {
                writer.writeLine(file.getAbsolutePath());
                if (applicable.size() > 0) {
                    writer.writeLine("\t APPLICABLE : " + applicable.stream().map(it -> it.getBomTool().getDescriptiveName()).sorted().collect(Collectors.joining(", ")));
                }
                if (extractable.size() > 0) {
                    writer.writeLine("\t EXTRACTABLE: " + extractable.stream().map(it -> it.getBomTool().getDescriptiveName()).sorted().collect(Collectors.joining(", ")));
                }
                if (extractionSuccess.size() > 0) {
                    writer.writeLine("\t SUCCESS    : " + extractionSuccess.stream().map(it -> it.getBomTool().getDescriptiveName()).sorted().collect(Collectors.joining(", ")));
                }
                if (extractionFailure.size() > 0) {
                    writer.writeLine("\t FAILURE    : " + extractionFailure.stream().map(it -> it.getBomTool().getDescriptiveName()).sorted().collect(Collectors.joining(", ")));
                }

            }
        }
        writer.writeLine(ReportConstants.HEADING);
        writer.writeLine("");
        writer.writeLine("");
    }
}
