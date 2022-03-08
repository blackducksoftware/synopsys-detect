package com.synopsys.integration.detect.workflow.report.output;

import com.google.gson.annotations.SerializedName;

public class FormattedCodeLocationOutput {
    @SerializedName("codeLocationName")
    public String codeLocationName;

    FormattedCodeLocationOutput(String name) {
        this.codeLocationName = name;
    }

}
