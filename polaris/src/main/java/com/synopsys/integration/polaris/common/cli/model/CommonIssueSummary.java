/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.cli.model;

import java.util.Map;

public class CommonIssueSummary {
    private Map<String, Integer> issuesBySeverity;
    private String summaryUrl;
    private Integer totalIssueCount;

    public Map<String, Integer> getIssuesBySeverity() {
        return issuesBySeverity;
    }

    public void setIssuesBySeverity(final Map<String, Integer> issuesBySeverity) {
        this.issuesBySeverity = issuesBySeverity;
    }

    public String getSummaryUrl() {
        return summaryUrl;
    }

    public void setSummaryUrl(final String summaryUrl) {
        this.summaryUrl = summaryUrl;
    }

    public Integer getTotalIssueCount() {
        return totalIssueCount;
    }

    public void setTotalIssueCount(final Integer totalIssueCount) {
        this.totalIssueCount = totalIssueCount;
    }

}
