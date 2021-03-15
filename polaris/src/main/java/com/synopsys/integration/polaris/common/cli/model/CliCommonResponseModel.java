/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.cli.model;

import java.util.List;
import java.util.Optional;

public class CliCommonResponseModel {
    private CommonScanInfo scanInfo;
    private CommonProjectInfo projectInfo;
    private CommonIssueSummary issueSummary;
    private List<CommonToolInfo> tools;

    public CommonScanInfo getScanInfo() {
        return scanInfo;
    }

    public void setScanInfo(final CommonScanInfo scanInfo) {
        this.scanInfo = scanInfo;
    }

    public CommonProjectInfo getProjectInfo() {
        return projectInfo;
    }

    public void setProjectInfo(final CommonProjectInfo projectInfo) {
        this.projectInfo = projectInfo;
    }

    public Optional<CommonIssueSummary> getIssueSummary() {
        return Optional.ofNullable(issueSummary);
    }

    public void setIssueSummary(final CommonIssueSummary issueSummary) {
        this.issueSummary = issueSummary;
    }

    public List<CommonToolInfo> getTools() {
        return tools;
    }

    public void setTools(final List<CommonToolInfo> tools) {
        this.tools = tools;
    }

}
