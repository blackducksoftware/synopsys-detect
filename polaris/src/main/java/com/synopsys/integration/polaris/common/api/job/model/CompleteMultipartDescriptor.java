/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.job.model;

import java.time.OffsetDateTime;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class CompleteMultipartDescriptor extends PolarisComponent {
    @SerializedName("url")
    private String url;

    @SerializedName("created")
    private OffsetDateTime created;

    @SerializedName("expiration")
    private OffsetDateTime expiration;

    @SerializedName("uploadId")
    private String uploadId;

    /**
     * Get url
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get created
     * @return created
     */
    public OffsetDateTime getCreated() {
        return created;
    }

    /**
     * Get expiration
     * @return expiration
     */
    public OffsetDateTime getExpiration() {
        return expiration;
    }

    /**
     * Get uploadId
     * @return uploadId
     */
    public String getUploadId() {
        return uploadId;
    }

}
