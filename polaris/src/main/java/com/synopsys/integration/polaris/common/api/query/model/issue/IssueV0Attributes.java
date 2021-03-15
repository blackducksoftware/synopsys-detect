/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.query.model.issue;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisAttributes;

public class IssueV0Attributes extends PolarisAttributes {
    @SerializedName("issue-key")
    private String issueKey;

    @SerializedName("finding-key")
    private String findingKey;

    @SerializedName("sub-tool")
    private String subTool;

    /**
     * &#x60;Automatic&#x60;.  The issue key assigned by the IM for this issue.  Uniquely identifies the issue.
     * @return issueKey
     */
    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(final String issueKey) {
        this.issueKey = issueKey;
    }

    /**
     * &#x60;Automatic&#x60;.  The finding key assigned by the TDS for this issue.  Redeemable from this issue&#39;s TDS.
     * @return findingKey
     */
    public String getFindingKey() {
        return findingKey;
    }

    public void setFindingKey(final String findingKey) {
        this.findingKey = findingKey;
    }

    /**
     * Specifies the sub tool used to find the issue
     * @return subTool
     */
    public String getSubTool() {
        return subTool;
    }

    public void setSubTool(final String subTool) {
        this.subTool = subTool;
    }

}

