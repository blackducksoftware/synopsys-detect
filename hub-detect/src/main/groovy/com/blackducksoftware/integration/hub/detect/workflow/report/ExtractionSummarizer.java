package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction.ExtractionResultType;

public class ExtractionSummarizer extends BomToolEvaluationSummarizer {
    public List<ExtractionSummaryData> summarize(final List<BomToolEvaluation> results, final Map<DetectCodeLocation, String> codeLocationNameMap) {
        final Map<File, List<BomToolEvaluation>> byDirectory = groupByDirectory(results);

        final List<ExtractionSummaryData> data = createSummaries(byDirectory, codeLocationNameMap);

        final List<ExtractionSummaryData> sorted = data.stream()
                .sorted((o1, o2) -> filesystemCompare(o1.getDirectory(), o2.getDirectory()))
                .collect(Collectors.toList());

        return sorted;
    }

    private List<ExtractionSummaryData> createSummaries(final Map<File, List<BomToolEvaluation>> byDirectory, final Map<DetectCodeLocation, String> codeLocationNameMap) {
        final List<ExtractionSummaryData> datas = new ArrayList<>();

        for (final Entry<File, List<BomToolEvaluation>> entry : byDirectory.entrySet()) {
            final ExtractionSummaryData data = new ExtractionSummaryData(entry.getKey().toString());
            entry.getValue().stream().forEach(it -> addDetail(data, it, codeLocationNameMap));
            datas.add(data);
        }

        return datas;
    }

    private void addDetail(final ExtractionSummaryData data, final BomToolEvaluation result, final Map<DetectCodeLocation, String> codeLocationNameMap) {
        if (result.isSearchable()) {
            data.searchable++;
        }
        if (result.isApplicable()) {
            data.applicable++;
        }
        if (result.isExtractable()) {
            data.extractable++;
        }
        if (result.getExtraction() != null) {
            addExtractionDetails(data, result, result.getExtraction(), codeLocationNameMap);
        }
    }

    private void addExtractionDetails(final ExtractionSummaryData data, final BomToolEvaluation result, final Extraction extraction, final Map<DetectCodeLocation, String> codeLocationNameMap) {
        if (extraction.result == ExtractionResultType.SUCCESS) {
            data.success.add(result);
        } else if (extraction.result == ExtractionResultType.FAILURE) {
            data.failed.add(result);
        } else if (extraction.result == ExtractionResultType.EXCEPTION) {
            data.exception.add(result);
        }
        data.codeLocationsExtracted += extraction.codeLocations.size();
        extraction.codeLocations.stream().forEach(it -> {
            final String name = codeLocationNameMap.get(it);
            data.codeLocationNames.add(name);
        });
    }

}
