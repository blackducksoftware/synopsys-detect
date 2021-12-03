package com.synopsys.integration.detect.workflow.report.output;

import com.google.gson.annotations.SerializedName;

public class FormattedExitCodeOutput {
    @SerializedName("exitCode")
    public Integer exitCode = 0;

    @SerializedName("exitCodeDescription")
    public String exitCodeDescription = "";

    @SerializedName("exitCodeReason")
    public String exitCodeReason = "";

    public FormattedExitCodeOutput(Integer exitCode, String exitCodeDescription, String exitCodeReason) {
        this.exitCode = exitCode;
        this.exitCodeDescription = exitCodeDescription;
        this.exitCodeReason = exitCodeReason;
    }
}
