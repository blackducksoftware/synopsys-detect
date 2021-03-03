/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.cli.model;

public class CommonScanInfo {
    private String cliVersion;
    private String scanTime;
    private String issueApiUrl;

    public String getCliVersion() {
        return cliVersion;
    }

    public void setCliVersion(final String cliVersion) {
        this.cliVersion = cliVersion;
    }

    public String getScanTime() {
        return scanTime;
    }

    public void setScanTime(final String scanTime) {
        this.scanTime = scanTime;
    }

    public String getIssueApiUrl() {
        return issueApiUrl;
    }

    public void setIssueApiUrl(final String issueApiUrl) {
        this.issueApiUrl = issueApiUrl;
    }

}
