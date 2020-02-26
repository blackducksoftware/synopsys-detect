package com.synopsys.integration.detect.workflow.report.output;

public class FormattedExitCodeOutput {
    public Integer exitCode = 0;
    public String exitCodeDescription = "";
    public String exitCodeReason = "";

    public FormattedExitCodeOutput(final Integer exitCode, String exitCodeDescription, final String exitCodeReason) {
        this.exitCode = exitCode;
        this.exitCodeDescription = exitCodeDescription;
        this.exitCodeReason = exitCodeReason;
    }
}
