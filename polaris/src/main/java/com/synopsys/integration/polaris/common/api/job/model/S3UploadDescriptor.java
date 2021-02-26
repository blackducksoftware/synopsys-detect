/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.job.model;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class S3UploadDescriptor extends PolarisComponent {
    @SerializedName("type")
    private String type;

    @SerializedName("key")
    private String key;

    @SerializedName("url")
    private String url;

    @SerializedName("bucket")
    private String bucket;

    @SerializedName("pathStyleAccess")
    private Boolean pathStyleAccess;

    @SerializedName("region")
    private String region;

    @SerializedName("expiration")
    private String expiration;

    /**
     * Get type
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Get key
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * Get url
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get bucket
     * @return bucket
     */
    public String getBucket() {
        return bucket;
    }

    /**
     * Get pathStyleAccess
     * @return pathStyleAccess
     */
    public Boolean getPathStyleAccess() {
        return pathStyleAccess;
    }

    /**
     * Get region
     * @return region
     */
    public String getRegion() {
        return region;
    }

    /**
     * Get expiration
     * @return expiration
     */
    public String getExpiration() {
        return expiration;
    }

}
