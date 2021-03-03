/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.report.output;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.detector.base.DetectorStatusCode;

public class FormattedDetectorOutput {
    @SerializedName("folder")
    public String folder = "";

    @SerializedName("detectorType")
    public String detectorType = "";

    @SerializedName("detectorName")
    public String detectorName = "";

    @SerializedName("descriptiveName")
    public String descriptiveName = "";

    @SerializedName("discoverable")
    public boolean discoverable = true;

    @SerializedName("extracted")
    public boolean extracted = true;

    @SerializedName("status")
    public String status = "";

    @SerializedName("statusCode")
    public DetectorStatusCode statusCode = DetectorStatusCode.UNKNOWN_DETECTOR_RESULT;

    @SerializedName("statusReason")
    public String statusReason = "";

    @SerializedName("discoveryReason")
    public String discoveryReason = "";

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

