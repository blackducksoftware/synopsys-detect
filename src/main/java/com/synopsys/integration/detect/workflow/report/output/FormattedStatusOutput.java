package com.synopsys.integration.detect.workflow.report.output;

public class FormattedStatusOutput {
    public final String key;
    public final String status;

    public FormattedStatusOutput(final String descriptionKey, final String status) {
        this.key = descriptionKey;
        this.status = status;
    }
}
