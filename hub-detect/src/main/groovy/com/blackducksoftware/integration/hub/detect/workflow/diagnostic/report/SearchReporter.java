package com.blackducksoftware.integration.hub.detect.workflow.diagnostic.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.report.ReportWriter;

public class SearchReporter {
    public void writeReport(final ReportWriter writer, final List<BomToolEvaluation> results) {
        final Map<File, List<BomToolEvaluation>> byDirectory = results.stream()
                .collect(Collectors.groupingBy(item -> item.getEnvironment().getDirectory()));

        printDirectories(writer, byDirectory);
    }

    private void printDirectories(final ReportWriter writer, final Map<File, List<BomToolEvaluation>> byDirectory) {
        for (final File file : byDirectory.keySet()) {
            final List<BomToolEvaluation> results = byDirectory.get(file);
            final List<String> toPrint = new ArrayList<>();

            for (final BomToolEvaluation result : results) {
                final String bomToolName = result.getBomTool().getDescriptiveName();
                if (result.isApplicable()) {
                    toPrint.add("      APPLIED: " + bomToolName + " - Search: " + result.getSearchabilityMessage() + " Applicable: " + result.getApplicabilityMessage());
                } else {

                    final String didNotApplyPrefix = "DID NOT APPLY: " + bomToolName + " - ";
                    if (BomToolEvaluation.NO_MESSAGE.equals(result.getApplicabilityMessage())) {
                        toPrint.add(didNotApplyPrefix + result.getSearchabilityMessage());
                    } else {
                        toPrint.add(didNotApplyPrefix + result.getApplicabilityMessage());
                    }
                }
            }
            if (toPrint.size() > 0) {

                writer.writeSeperator();
                writer.writeLine("Detailed search results for directory");
                writer.writeLine(file.getAbsolutePath());
                writer.writeSeperator();
                toPrint.stream().sorted().forEach(it -> writer.writeLine(it));
                writer.writeSeperator();
            }
        }
    }
}
