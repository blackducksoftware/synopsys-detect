package com.blackducksoftware.integration.hub.detect.workflow.bdio;

public class BdioOptions {
    private final String bdioAggregateName;

    public BdioOptions(final String bdioAggregateName) {
        this.bdioAggregateName = bdioAggregateName;
    }

    public String getBdioAggregateName() {
        return bdioAggregateName;
    }
}
