package com.synopsys.integration.detect.workflow.report;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detect.tool.detector.report.DetectorDirectoryReport;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;

public class DetailedSearchSummaryReporter { //TODO (detectors): Can't exactly summarize the complete ones.
    public void print(ReportWriter writer, List<DetectorDirectoryReport> reports) { //This is where the magic happens
        //we want EXTRACTED, NOT EXTRACTED, NOT FOUND, etc
        for (DetectorDirectoryReport report : reports) {
            List<String> toPrint = new ArrayList<>();

            report.getExtractedDetectors().forEach(extracted -> {
                toPrint.add(" EXTRACTED: " + extracted.getRule().getDetectorType() + " - " + extracted.getExtractedDetectable().getDetectable().getName());
                extracted.getAttemptedDetectables().forEach(attempted -> {
                    toPrint.add(" ATTEMPTED: " + extracted.getRule().getDetectorType() + " - "
                        + attempted.getDetectable().getName() + " - " + attempted.getStatusCode() + " - " + attempted.getStatusReason());
                });
            });
            report.getNotExtractedDetectors().forEach(notExtracted -> {
                notExtracted.getAttemptedDetectables().forEach(attempted -> {
                    toPrint.add(" ATTEMPTED: " + notExtracted.getRule().getDetectorType() + " - "
                        + attempted.getDetectable().getName() + " - " + attempted.getStatusCode() + " - " + attempted.getStatusReason());
                });

            });
            report.getNotFoundDetectors().forEach(notFound -> {
                toPrint.add(" NOT FOUND: " + notFound.getDetectorRule().getDetectorType() + " - " + notFound.getReasons());
            });

            if (toPrint.size() > 0) {
                writer.writeSeparator();
                writer.writeLine("Detailed search results for directory");
                writer.writeLine(report.getDirectory().toString());
                writer.writeSeparator();
                toPrint.stream().sorted().forEach(writer::writeLine);
                writer.writeSeparator();
            }
        }
    }
}
