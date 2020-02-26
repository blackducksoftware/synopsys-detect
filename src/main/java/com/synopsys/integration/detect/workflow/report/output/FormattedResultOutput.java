package com.synopsys.integration.detect.workflow.report.output;

public class FormattedResultOutput {
    public String location;
    public String message;

    public FormattedResultOutput(final String location, final String message) {
        this.location = location;
        this.message = message;
    }
}
