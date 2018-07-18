package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.report.DetailedSearchSummaryData.BomToolSearchDetails;

public class DetailedSearchSummarizer extends BomToolEvaluationSummarizer {
    public List<DetailedSearchSummaryData> summarize(final List<BomToolEvaluation> bomToolEvaluations) {
        final Map<File, List<BomToolEvaluation>> byDirectory = groupByDirectory(bomToolEvaluations);
        final List<DetailedSearchSummaryData> data = createSummaries(byDirectory);
        final List<DetailedSearchSummaryData> sorted = data.stream()
                .sorted((o1, o2) -> filesystemCompare(o1.directory, o2.directory))
                .collect(Collectors.toList());
        return sorted;
    }

    private List<DetailedSearchSummaryData> createSummaries(final Map<File, List<BomToolEvaluation>> byDirectory) {
        final List<DetailedSearchSummaryData> datas = new ArrayList<>();
        for (final Entry<File, List<BomToolEvaluation>> entry : byDirectory.entrySet()) {
            final DetailedSearchSummaryData data = new DetailedSearchSummaryData();
            data.directory = entry.getKey().toString();
            for (final BomToolEvaluation evaluation : entry.getValue()) {
                final BomToolSearchDetails detail = data.new BomToolSearchDetails();
                detail.bomTool = evaluation.getBomTool();
                if (evaluation.isSearchable()) {
                    if (evaluation.isApplicable()) {
                        detail.reason = "Search: " + evaluation.getSearchabilityMessage() + " Applicable: " + evaluation.getApplicabilityMessage();
                        data.applicable.add(detail);
                    } else {
                        detail.reason = evaluation.getApplicabilityMessage();
                        data.notApplicable.add(detail);
                    }
                } else {
                    detail.reason = evaluation.getSearchabilityMessage();
                    data.notSearchable.add(detail);
                }
            }
            datas.add(data);
        }
        return datas;
    }
}
