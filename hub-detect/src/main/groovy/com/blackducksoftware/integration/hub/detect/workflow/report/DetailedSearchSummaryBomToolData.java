package com.blackducksoftware.integration.hub.detect.workflow.report;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;

public class DetailedSearchSummaryBomToolData {
    private final BomTool bomTool;
    private final String reason;

    public DetailedSearchSummaryBomToolData(final BomTool bomTool, final String reason) {
        this.bomTool = bomTool;
        this.reason = reason;
    }

    public BomTool getBomTool() {
        return bomTool;
    }

    public String getReason() {
        return reason;
    }
}
