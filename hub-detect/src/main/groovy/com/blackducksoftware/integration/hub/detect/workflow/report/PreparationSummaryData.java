package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.util.List;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;

public class PreparationSummaryData {
    private final String directory;

    private final List<BomToolEvaluation> ready;
    private final List<BomToolEvaluation> failed;

    public PreparationSummaryData(final String directory, final List<BomToolEvaluation> ready, final List<BomToolEvaluation> failed) {
        this.directory = directory;
        this.ready = ready;
        this.failed = failed;
    }

    public String getDirectory() {
        return directory;
    }

    public List<BomToolEvaluation> getReady() {
        return ready;
    }

    public List<BomToolEvaluation> getFailed() {
        return failed;
    }
}
