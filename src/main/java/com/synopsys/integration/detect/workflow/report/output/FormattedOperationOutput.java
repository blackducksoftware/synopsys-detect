package com.synopsys.integration.detect.workflow.report.output;

public class FormattedOperationOutput {
    public final String startTimestamp;
    public final String endTimestamp;
    public final String descriptionKey;
    public final String status;

    public FormattedOperationOutput(String startTimestamp, String endTimestamp, String descriptionKey, String status) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.descriptionKey = descriptionKey;
        this.status = status;
    }
}
