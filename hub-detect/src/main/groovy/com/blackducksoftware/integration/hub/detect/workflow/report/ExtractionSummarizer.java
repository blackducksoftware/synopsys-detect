package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
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
        return byDirectory.entrySet().stream()
                .map(it -> createData(it.getKey().toString(), it.getValue(), codeLocationNameMap))
                .collect(Collectors.toList());

    }

    private ExtractionSummaryData createData(final String directory, final List<BomToolEvaluation> evaluations, final Map<DetectCodeLocation, String> codeLocationNameMap) {
        final List<BomToolEvaluation> extractions = evaluations.stream()
                .filter(it -> it.getExtraction() != null)
                .collect(Collectors.toList());

        final List<BomToolEvaluation> success = extractions.stream()
                .filter(it -> it.getExtraction().result == ExtractionResultType.SUCCESS)
                .collect(Collectors.toList());

        final List<BomToolEvaluation> failure = extractions.stream()
                .filter(it -> it.getExtraction().result == ExtractionResultType.FAILURE)
                .collect(Collectors.toList());

        final List<BomToolEvaluation> exception = extractions.stream()
                .filter(it -> it.getExtraction().result == ExtractionResultType.EXCEPTION)
                .collect(Collectors.toList());

        final List<String> codeLocationNames = extractions.stream()
                .flatMap(it -> it.getExtraction().codeLocations.stream())
                .map(codeLocation -> codeLocationNameMap.get(codeLocation))
                .collect(Collectors.toList());

        return new ExtractionSummaryData(directory, success, failure, exception, codeLocationNames);
    }

}
