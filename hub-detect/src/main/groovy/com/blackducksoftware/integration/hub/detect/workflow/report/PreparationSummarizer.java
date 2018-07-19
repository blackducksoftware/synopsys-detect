package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;

public class PreparationSummarizer extends BomToolEvaluationSummarizer {

    public List<PreparationSummaryData> summarize(final List<BomToolEvaluation> results) {
        final Map<File, List<BomToolEvaluation>> byDirectory = groupByDirectory(results);

        final List<PreparationSummaryData> data = summarizeDirectory(byDirectory);

        final List<PreparationSummaryData> sorted = data.stream()
                .sorted((o1, o2) -> filesystemCompare(o1.getDirectory(), o2.getDirectory()))
                .collect(Collectors.toList());

        return sorted;
    }

    private List<PreparationSummaryData> summarizeDirectory(final Map<File, List<BomToolEvaluation>> byDirectory) {
        return byDirectory.entrySet().stream()
                .map(it -> createData(it.getKey().toString(), it.getValue()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<PreparationSummaryData> createData(final String directory, final List<BomToolEvaluation> evaluations) {
        final List<BomToolEvaluation> applicable = evaluations.stream()
                .filter(it -> it.isApplicable())
                .collect(Collectors.toList());

        final List<BomToolEvaluation> ready = applicable.stream()
                .filter(it -> it.isExtractable())
                .collect(Collectors.toList());

        final List<BomToolEvaluation> failed = applicable.stream()
                .filter(it -> !it.isExtractable())
                .collect(Collectors.toList());

        if (ready.size() > 0 || failed.size() > 0) {
            final PreparationSummaryData data = new PreparationSummaryData(directory, ready, failed);
            return Optional.of(data);
        } else {
            return Optional.empty();
        }
    }

}
