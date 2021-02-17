/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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

