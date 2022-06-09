package com.synopsys.integration.detect.workflow.report;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.detect.tool.detector.report.DetectorDirectoryReport;
import com.synopsys.integration.detect.tool.detector.report.rule.EvaluatedDetectorRuleReport;
import com.synopsys.integration.detect.workflow.report.util.ReporterUtils;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detector.rule.DetectorRule;

public class SearchSummaryReporter {
    public void print(ReportWriter writer, List<DetectorDirectoryReport> reports) {
        ReporterUtils.printHeader(writer, "Search results");
        boolean printedAtLeastOne = false;
        for (DetectorDirectoryReport report : reports) {
            Set<DetectorRule> found = new HashSet<>();
            report.getExtractedDetectors().stream()
                .map(EvaluatedDetectorRuleReport::getRule)
                .forEach(found::add);

            report.getNotExtractedDetectors().stream()
                .map(EvaluatedDetectorRuleReport::getRule)
                .forEach(found::add);

            if (found.size() > 0) {
                writer.writeLine(report.getDirectory().toString());
                writer.writeLine("\tFOUND: " + found.stream().map(it -> it.getDetectorType().toString()).sorted().collect(Collectors.joining(", ")));
                printedAtLeastOne = true;
            }
        }
        if (!printedAtLeastOne) {
            writer.writeLine("No detectors found.");
        }
        ReporterUtils.printFooter(writer);
    }

}
