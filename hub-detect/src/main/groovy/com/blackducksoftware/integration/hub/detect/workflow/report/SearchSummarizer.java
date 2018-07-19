package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;

public class SearchSummarizer extends BomToolEvaluationSummarizer {
    public List<SearchSummaryData> summarize(final List<BomToolEvaluation> bomToolEvaluations) {
        final Map<File, List<BomToolEvaluation>> byDirectory = groupByDirectory(bomToolEvaluations);
        final List<SearchSummaryData> data = createSummaries(byDirectory);
        final List<SearchSummaryData> sorted = data.stream()
                .sorted((o1, o2) -> filesystemCompare(o1.getDirectory(), o2.getDirectory()))
                .collect(Collectors.toList());
        return sorted;
    }

    private List<SearchSummaryData> createSummaries(final Map<File, List<BomToolEvaluation>> byDirectory) {
        return byDirectory.entrySet().stream()
                .map(it -> createData(it.getKey().toString(), it.getValue()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<SearchSummaryData> createData(final String directory, final List<BomToolEvaluation> evaluations) {
        final List<BomTool> applicable = evaluations.stream()
                .filter(it -> it.isApplicable())
                .map(it -> it.getBomTool())
                .collect(Collectors.toList());

        if (applicable.size() > 0) {
            return Optional.of(new SearchSummaryData(directory, applicable));
        } else {
            return Optional.empty();
        }

    }
}
