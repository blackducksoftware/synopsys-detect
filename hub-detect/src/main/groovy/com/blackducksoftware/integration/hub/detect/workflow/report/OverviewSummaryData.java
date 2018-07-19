package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.util.List;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;

public class OverviewSummaryData {
    private final String directory;
    private final List<BomToolEvaluation> applicable;
    private final List<BomToolEvaluation> extractable;
    private final List<BomToolEvaluation> extractionSuccess;
    private final List<BomToolEvaluation> extractionFailure;

    public OverviewSummaryData(final String directory, final List<BomToolEvaluation> applicable, final List<BomToolEvaluation> extractable, final List<BomToolEvaluation> extractionSuccess, final List<BomToolEvaluation> extractionFailure) {
        this.directory = directory;
        this.applicable = applicable;
        this.extractable = extractable;
        this.extractionSuccess = extractionSuccess;
        this.extractionFailure = extractionFailure;
    }

    public List<BomToolEvaluation> getApplicable() {
        return applicable;
    }

    public List<BomToolEvaluation> getExtractable() {
        return extractable;
    }

    public List<BomToolEvaluation> getExtractionSuccess() {
        return extractionSuccess;
    }

    public List<BomToolEvaluation> getExtractionFailure() {
        return extractionFailure;
    }

    public String getDirectory() {
        return directory;
    }
}
