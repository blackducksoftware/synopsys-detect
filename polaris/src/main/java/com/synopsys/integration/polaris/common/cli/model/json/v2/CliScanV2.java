/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.cli.model.json.v2;

import com.synopsys.integration.polaris.common.cli.model.json.CliScanResponse;
import com.synopsys.integration.polaris.common.cli.model.json.v1.IssueSummaryV1;
import com.synopsys.integration.polaris.common.cli.model.json.v1.ProjectInfoV1;
import com.synopsys.integration.polaris.common.cli.model.json.v1.ScanInfoV1;

import java.util.List;

public class CliScanV2 implements CliScanResponse {
    public String version;
    public ScanInfoV1 scanInfo;
    public ProjectInfoV1 projectInfo;
    public IssueSummaryV1 issueSummary;
    public List<ToolInfoV2> tools;

}
