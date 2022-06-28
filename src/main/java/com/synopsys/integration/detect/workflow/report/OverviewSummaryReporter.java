package com.synopsys.integration.detect.workflow.report;

import java.util.List;

import com.synopsys.integration.detect.tool.detector.report.DetectorDirectoryReport;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;

public class OverviewSummaryReporter {
    public void writeReport(ReportWriter writer, List<DetectorDirectoryReport> reports) {
        writer.writeSeparator();
        for (DetectorDirectoryReport report : reports) {
            writer.writeLine("DIRECTORY: " + report.getDirectory());
            //Extracted
            report.getExtractedDetectors().forEach(extracted -> {
                writer.writeLine("DETECTOR: " + extracted.getRule().getDetectorType());
                writer.writeLine("EXTRACTED: " + extracted.getExtractedDetectable().getDetectable().getName());
                writer.writeLine("\tEXTRACTION: " + extracted.getExtractedDetectable().getExtraction().getCodeLocations().size() + " Code Locations");
                extracted.getExtractedDetectable().getExplanations().forEach(explanation -> {
                    writer.writeLine("\t\t" + explanation.describeSelf());
                });

                //Attempted
                extracted.getAttemptedDetectables().forEach(attempted -> {
                    writer.writeLine("ATTEMPTED: " + attempted.getDetectable().getName());
                    writer.writeLine("\tREASON: " + attempted.getStatusReason());
                    attempted.getExplanations().forEach(explanation -> {
                        writer.writeLine("\t\t" + explanation.describeSelf());
                    });
                });
            });

            //Not Extracted
            report.getNotExtractedDetectors().forEach(extracted -> {
                writer.writeLine("DETECTOR: " + extracted.getRule().getDetectorType());
                //Attempted
                extracted.getAttemptedDetectables().forEach(attempted -> {
                    writer.writeLine("ATTEMPTED: " + attempted.getDetectable().getName());
                    writer.writeLine("\tREASON: " + attempted.getStatusReason());
                    attempted.getExplanations().forEach(explanation -> {
                        writer.writeLine("\t\t" + explanation.describeSelf());
                    });
                });
            });

            //TODO (detector): Should we still capture the detectable instance?
            //                    Map<String, String> data = new HashMap<>();
            //                    ObjectPrinter.populateObjectPrivate(null, detectableEvaluation.getDetectab.get(), data);
            //                    data.forEach((key, value) -> writer.writeLine("\t" + key + ": " + value));

        }
        writer.writeLine(ReportConstants.HEADING);
        writer.writeLine("");
        writer.writeLine("");
    }

}
