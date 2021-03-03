/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.cli.model.json.v1;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.cli.model.json.CliScanResponse;

public class CliScanV1 implements CliScanResponse {
    public String version;
    public ScanInfoV1 scanInfo;
    public ProjectInfoV1 projectInfo;
    public IssueSummaryV1 issueSummary;

    @SerializedName("coverity")
    public ToolInfoV1 coverityToolInfo;

    @SerializedName("sca")
    public ToolInfoV1 blackDuckScaToolInfo;

}
