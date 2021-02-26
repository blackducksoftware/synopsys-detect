/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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

