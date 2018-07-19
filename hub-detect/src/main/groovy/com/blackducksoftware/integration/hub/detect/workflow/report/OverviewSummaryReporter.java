package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.util.List;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.ReportConstants;

public class OverviewSummaryReporter {

    public void writeReport(final ReportWriter writer, final List<BomToolEvaluation> results) {
        final OverviewSummarizer summarizer = new OverviewSummarizer();

        final List<OverviewSummaryData> summaries = summarizer.summarize(results);

        writeSummaries(writer, summaries);

    }

    private void writeSummaries(final ReportWriter writer, final List<OverviewSummaryData> summaries) {
        writer.writeSeperator();
        for (final OverviewSummaryData data : summaries) {
            if (data.getApplicable().size() > 0) {
                writer.writeLine(data.getDirectory());
                if (data.getApplicable().size() > 0) {
                    printEvaluations(writer, "\t APPLICABLE : ", data.getApplicable());
                }
                if (data.getExtractable().size() > 0) {
                    printEvaluations(writer, "\t EXTRACTABLE: ", data.getExtractable());
                }
                if (data.getExtractionSuccess().size() > 0) {
                    printEvaluations(writer, "\t SUCCESS    : ", data.getExtractionSuccess());
                }
                if (data.getExtractionFailure().size() > 0) {
                    printEvaluations(writer, "\t FAILURE    : ", data.getExtractionFailure());
                }
            }
        }
        writer.writeLine(ReportConstants.HEADING);
        writer.writeLine("");
        writer.writeLine("");
    }

    private void printEvaluations(final ReportWriter writer, final String prefix, final List<BomToolEvaluation> evaluations) {
        writer.writeLine("\t APPLICABLE : " + evaluations.stream().map(it -> it.getBomTool().getDescriptiveName()).sorted().collect(Collectors.joining(", ")));
    }
}
