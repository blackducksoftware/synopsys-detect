package com.synopsys.integration.detect.tool.signaturescanner.operation;

import com.google.gson.annotations.SerializedName;

public class SignatureScanRapidResult {
    @SerializedName("version")
    public String version;

    @SerializedName("scanId")
    public String scanId;
    
    @SerializedName("exitStatus")
    public String exitStatus;
}
