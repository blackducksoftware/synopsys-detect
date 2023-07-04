package com.synopsys.integration.detect.workflow.report.output;

import java.util.UUID;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.detect.workflow.status.FormattedCodeLocation;

public class FormattedCodeLocationOutput {
    @SerializedName("codeLocationName")
    public String codeLocationName;
    
    @SerializedName("scanId")
    public UUID scanId;
    
    @SerializedName("scanType")
    public String scanType;

    FormattedCodeLocationOutput(FormattedCodeLocation data) {
        this.codeLocationName = data.getCodeLocationName();
        this.scanId = data.getScanId();
        this.scanType = data.getScanType();
    }
}