package com.synopsys.integration.detect.workflow.report.output;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.detector.base.DetectorStatusCode;

//throw in the Success (only see the success) or All failures
//throw in the Attempted (thats what is missing)
public class FormattedDetectorOutput {
    @SerializedName("folder")
    public String folder = "";

    @SerializedName("detectorType")
    public String detectorType = "";

    @SerializedName("detectorName")
    public String detectorName = "";
    
    @SerializedName("detectorAccuracy")
    public String detectorAccuracy = "";

    @SerializedName("extracted")
    public boolean extracted = true;

    @SerializedName("status")
    public String status = "";

    @SerializedName("statusCode")
    public DetectorStatusCode statusCode = DetectorStatusCode.UNKNOWN_DETECTOR_RESULT;

    @SerializedName("statusReason")
    public String statusReason = "";

    @SerializedName("extractedReason")
    public String extractedReason = "";

    @SerializedName("relevantFiles")
    public List<String> relevantFiles = new ArrayList<>();

    @SerializedName("explanations")
    public List<String> explanations = new ArrayList<>();

    @SerializedName("projectName")
    public String projectName = "";

    @SerializedName("projectVersion")
    public String projectVersion = "";

    @SerializedName("codeLocationCount")
    public int codeLocationCount = 0;

}

