package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;

public class DetailedSearchSummarizer extends BomToolEvaluationSummarizer {
    public List<DetailedSearchSummaryData> summarize(final List<BomToolEvaluation> bomToolEvaluations) {
        final Map<File, List<BomToolEvaluation>> byDirectory = groupByDirectory(bomToolEvaluations);
        final List<DetailedSearchSummaryData> data = createSummaries(byDirectory);
        final List<DetailedSearchSummaryData> sorted = data.stream()
                .sorted((o1, o2) -> filesystemCompare(o1.getDirectory(), o2.getDirectory()))
                .collect(Collectors.toList());
        return sorted;
    }

    private List<DetailedSearchSummaryData> createSummaries(final Map<File, List<BomToolEvaluation>> byDirectory) {
        final List<DetailedSearchSummaryData> datas = new ArrayList<>();
        for (final Entry<File, List<BomToolEvaluation>> entry : byDirectory.entrySet()) {
            final DetailedSearchSummaryData data = createData(entry.getKey().toString(), entry.getValue());
            datas.add(data);
        }
        return datas;
    }

    private DetailedSearchSummaryData createData(final String directory, final List<BomToolEvaluation> evaluations) {
        final List<DetailedSearchSummaryBomToolData> applicable = new ArrayList<>();
        final List<DetailedSearchSummaryBomToolData> notApplicable = new ArrayList<>();
        final List<DetailedSearchSummaryBomToolData> notSearchable = new ArrayList<>();

        for (final BomToolEvaluation evaluation : evaluations) {
            if (evaluation.isApplicable()) {
                final String reason = "Search: " + evaluation.getSearchabilityMessage() + " Applicable: " + evaluation.getApplicabilityMessage();
                applicable.add(new DetailedSearchSummaryBomToolData(evaluation.getBomTool(), reason));
            } else if (evaluation.isSearchable()) {
                final String reason = evaluation.getApplicabilityMessage();
                notApplicable.add(new DetailedSearchSummaryBomToolData(evaluation.getBomTool(), reason));
            } else {
                final String reason = evaluation.getSearchabilityMessage();
                notSearchable.add(new DetailedSearchSummaryBomToolData(evaluation.getBomTool(), reason));
            }
        }

        return new DetailedSearchSummaryData(directory, applicable, notApplicable, notSearchable);
    }
}
