package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;

public class OverviewSummarizer extends BomToolEvaluationSummarizer {

    public List<OverviewSummaryData> summarize(final List<BomToolEvaluation> evaluations) {
        final Map<File, List<BomToolEvaluation>> byDirectory = groupByDirectory(evaluations);

        final List<OverviewSummaryData> summaries = summarize(byDirectory);

        final List<OverviewSummaryData> sorted = summaries.stream()
                .sorted((o1, o2) -> filesystemCompare(o1.getDirectory(), o2.getDirectory()))
                .collect(Collectors.toList());

        return sorted;

    }

    private List<OverviewSummaryData> summarize(final Map<File, List<BomToolEvaluation>> byDirectory) {
        return byDirectory.entrySet().stream()
                .map(it -> createData(it.getKey().toString(), it.getValue()))
                .collect(Collectors.toList());
    }

    private OverviewSummaryData createData(final String directory, final List<BomToolEvaluation> evaluations) {
        final List<BomToolEvaluation> applicable = evaluations.stream()
                .filter(it -> it.isApplicable())
                .collect(Collectors.toList());

        final List<BomToolEvaluation> extractable = applicable.stream()
                .filter(it -> it.isExtractable())
                .collect(Collectors.toList());

        final List<BomToolEvaluation> extractionSuccess = extractable.stream()
                .filter(it -> it.wasExtractionSuccessful())
                .collect(Collectors.toList());

        final List<BomToolEvaluation> extractionFailure = extractable.stream()
                .filter(it -> !it.wasExtractionSuccessful())
                .collect(Collectors.toList());

        return new OverviewSummaryData(directory, applicable, extractable, extractionSuccess, extractionFailure);
    }

}
