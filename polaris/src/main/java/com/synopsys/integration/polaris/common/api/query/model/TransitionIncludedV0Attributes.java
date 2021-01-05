/**
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
package com.synopsys.integration.polaris.common.api.query.model;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class TransitionIncludedV0Attributes extends PolarisComponent {
    @SerializedName("transition-type")
    private String transitionType;

    @SerializedName("cause")
    private String cause;

    @SerializedName("transition-date")
    private String transitionDate;

    @SerializedName("branch-id")
    private String branchId;

    @SerializedName("revision-id")
    private String revisionId;

    @SerializedName("run-id")
    private String runId;

    /**
     * The transition type, either: &#39;opened&#39; or &#39;closed&#39;
     * @return transitionType
     */
    public String getTransitionType() {
        return transitionType;
    }

    public void setTransitionType(final String transitionType) {
        this.transitionType = transitionType;
    }

    /**
     * The probable cause of the transition, for example &#39;LEGACY&#39;, &#39;SUT_CHANGE&#39;
     * @return cause
     */
    public String getCause() {
        return cause;
    }

    public void setCause(final String cause) {
        this.cause = cause;
    }

    /**
     * The date when the transition was observed
     * @return transitionDate
     */
    public String getTransitionDate() {
        return transitionDate;
    }

    public void setTransitionDate(final String transitionDate) {
        this.transitionDate = transitionDate;
    }

    /**
     * The ID of the branch that this transition was observed on
     * @return branchId
     */
    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(final String branchId) {
        this.branchId = branchId;
    }

    /**
     * The ID of the revision that this transition was observed on (optional)
     * @return revisionId
     */
    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(final String revisionId) {
        this.revisionId = revisionId;
    }

    /**
     * The ID of the run that this transition was observed on
     * @return runId
     */
    public String getRunId() {
        return runId;
    }

    public void setRunId(final String runId) {
        this.runId = runId;
    }

}

