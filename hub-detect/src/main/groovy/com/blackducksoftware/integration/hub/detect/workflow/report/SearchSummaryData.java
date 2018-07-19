package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.util.List;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;

public class SearchSummaryData {
    private final String directory;
    private final List<BomTool> applicable;

    public SearchSummaryData(final String directory, final List<BomTool> applicable) {
        this.directory = directory;
        this.applicable = applicable;
    }

    public String getDirectory() {
        return directory;
    }

    public List<BomTool> getApplicable() {
        return applicable;
    }

}
