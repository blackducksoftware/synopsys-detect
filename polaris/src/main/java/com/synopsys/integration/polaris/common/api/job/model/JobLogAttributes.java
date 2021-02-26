/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.job.model;

import java.time.OffsetDateTime;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisAttributes;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class JobLogAttributes extends PolarisAttributes {
    @SerializedName("analysisOutputUrl")
    private String analysisOutputUrl;

    @SerializedName("expiration")
    private OffsetDateTime expiration;

    @SerializedName("jobLogUrl")
    private String jobLogUrl;

    /**
     * Get analysisOutputUrl
     * @return analysisOutputUrl
     */
    public String getAnalysisOutputUrl() {
        return analysisOutputUrl;
    }

    public void setAnalysisOutputUrl(String analysisOutputUrl) {
        this.analysisOutputUrl = analysisOutputUrl;
    }

    /**
     * Get expiration
     * @return expiration
     */
    public OffsetDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(OffsetDateTime expiration) {
        this.expiration = expiration;
    }

    /**
     * Get jobLogUrl
     * @return jobLogUrl
     */
    public String getJobLogUrl() {
        return jobLogUrl;
    }

    public void setJobLogUrl(String jobLogUrl) {
        this.jobLogUrl = jobLogUrl;
    }

}
