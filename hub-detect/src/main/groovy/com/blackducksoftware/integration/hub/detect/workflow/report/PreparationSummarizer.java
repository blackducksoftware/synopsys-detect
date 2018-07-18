package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;

public class PreparationSummarizer extends BomToolEvaluationSummarizer {

    public List<PreparationSummaryData> summarize(final List<BomToolEvaluation> results) {
        final Map<File, List<BomToolEvaluation>> byDirectory = groupByDirectory(results);

        final List<PreparationSummaryData> data = summarizeDirectory(byDirectory);

        final List<PreparationSummaryData> sorted = data.stream()
                .sorted((o1, o2) -> filesystemCompare(o1.directory, o2.directory))
                .collect(Collectors.toList());

        return sorted;
    }

    private List<PreparationSummaryData> summarizeDirectory(final Map<File, List<BomToolEvaluation>> byDirectory) {
        final List<PreparationSummaryData> datas = new ArrayList<>();
        for (final File file : byDirectory.keySet()) {
            final List<BomToolEvaluation> results = byDirectory.get(file);

            final PreparationSummaryData data = new PreparationSummaryData();
            data.directory = file.toString();
            data.ready = new ArrayList<>();
            data.failed = new ArrayList<>();

            for (final BomToolEvaluation result : results) {
                if (result.isApplicable()) {
                    if (result.isExtractable()) {
                        data.ready.add(result);
                    } else {
                        data.failed.add(result);
                    }
                }
            }
            if (data.ready.size() > 0 || data.failed.size() > 0) {
                datas.add(data);
            }
        }
        return datas;
    }

}
