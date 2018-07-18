package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction.ExtractionResultType;

public class ExtractionSummarizer extends BomToolEvaluationSummarizer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<ExtractionSummaryData> summarize(final List<BomToolEvaluation> results, final Map<DetectCodeLocation, String> codeLocationNameMap) {
        final Map<File, List<BomToolEvaluation>> byDirectory = groupByDirectory(results);

        final List<ExtractionSummaryData> data = createSummaries(byDirectory, codeLocationNameMap);

        final List<ExtractionSummaryData> sorted = data.stream()
                .sorted((o1, o2) -> filesystemCompare(o1.directory, o2.directory))
                .collect(Collectors.toList());

        return sorted;
    }

    private List<ExtractionSummaryData> createSummaries(final Map<File, List<BomToolEvaluation>> byDirectory, final Map<DetectCodeLocation, String> codeLocationNameMap) {
        final List<ExtractionSummaryData> datas = new ArrayList<>();

        for (final File file : byDirectory.keySet()) {
            final List<BomToolEvaluation> results = byDirectory.get(file);

            final ExtractionSummaryData data = new ExtractionSummaryData();
            data.directory = file.toString();
            datas.add(data);

            for (final BomToolEvaluation result : results) {
                if (result.isSearchable()) {
                    data.searchable++;
                }
                if (result.isApplicable()) {
                    data.applicable++;
                }
                if (result.isExtractable()) {
                    data.extractable++;

                    if (result.getExtraction() != null) {
                        final Extraction extraction = result.getExtraction();
                        data.codeLocationsExtracted += extraction.codeLocations.size();
                        extraction.codeLocations.stream().forEach(it -> {
                            final String name = codeLocationNameMap.get(it);
                            data.codeLocationNames.add(name);
                        });
                        if (extraction.result == ExtractionResultType.SUCCESS) {
                            data.success.add(result);
                        } else if (extraction.result == ExtractionResultType.FAILURE) {
                            data.failed.add(result);
                        } else if (extraction.result == ExtractionResultType.EXCEPTION) {
                            data.exception.add(result);
                        }
                    } else {
                        logger.warn("A bomTool was searchable, applicable and extractable but produced no extraction.");
                    }
                }
            }
        }

        return datas;
    }

}
