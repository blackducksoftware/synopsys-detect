package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;

public class SearchSummarizer extends BomToolEvaluationSummarizer {
    public List<SearchSummaryData> summarize(final List<BomToolEvaluation> bomToolEvaluations) {
        final Map<File, List<BomToolEvaluation>> byDirectory = groupByDirectory(bomToolEvaluations);
        final List<SearchSummaryData> data = createSummaries(byDirectory);
        final List<SearchSummaryData> sorted = data.stream()
                .sorted((o1, o2) -> filesystemCompare(o1.directory, o2.directory))
                .collect(Collectors.toList());
        return sorted;
    }

    private List<SearchSummaryData> createSummaries(final Map<File, List<BomToolEvaluation>> byDirectory) {
        final List<SearchSummaryData> datas = new ArrayList<>();
        for (final Entry<File, List<BomToolEvaluation>> entry : byDirectory.entrySet()) {
            final SearchSummaryData data = new SearchSummaryData();
            data.directory = entry.getKey().toString();
            data.applicable = entry.getValue().stream()
                    .filter(it -> it.isApplicable())
                    .map(it -> it.getBomTool())
                    .collect(Collectors.toList());
            if (data.applicable.size() > 0) {
                datas.add(data);
            }
        }
        return datas;
    }
}
