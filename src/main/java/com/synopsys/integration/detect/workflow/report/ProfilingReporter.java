package com.synopsys.integration.detect.workflow.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.workflow.profiling.DetectorTimings;
import com.synopsys.integration.detect.workflow.profiling.Timing;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detector.base.DetectorType;

public class ProfilingReporter {
    private static final int PADDING_LENGTH = 30;

    public void writeReport(ReportWriter writer, DetectorTimings detectorTimings) {
        writer.writeSeparator();
        writer.writeLine("Applicable Times");
        writer.writeSeparator();
        writeAggregateReport(writer, detectorTimings.getApplicableTimings());
        writer.writeSeparator();
        writer.writeLine("Extractable Times");
        writer.writeSeparator();
        writeReport(writer, detectorTimings.getExtractableTimings());
        writer.writeSeparator();
        writer.writeLine("Extraction Times");
        writer.writeSeparator();
        writeReport(writer, detectorTimings.getExtractionTimings());
    }

    private void writeAggregateReport(ReportWriter writer, List<Timing<DetectorType>> timings) {
        Map<String, Long> aggregated = new HashMap<>();

        for (Timing<DetectorType> detectorTime : timings) {
            String name = detectorTime.getKey().toString();
            if (!aggregated.containsKey(name)) {
                aggregated.put(name, 0L);
            }
            aggregated.put(name, aggregated.get(name) + detectorTime.getMs());
        }

        for (Map.Entry<String, Long> aggregatedEntry : aggregated.entrySet()) {
            writer.writeLine("\t" + padToLength(aggregatedEntry.getKey(), PADDING_LENGTH) + "\t" + aggregatedEntry.getValue());
        }
    }

    private void writeReport(ReportWriter writer, List<Timing<DetectorType>> timings) {
        for (Timing<DetectorType> detectorTime : timings) {
            writer.writeLine("\t" + padToLength(detectorTime.getKey().toString(), PADDING_LENGTH) + "\t" + detectorTime.getMs());
        }
    }

    private String padToLength(String text, int length) {
        return StringUtils.rightPad(text, length);
    }
}
