/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.job.model;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class RoleAssumerS3UploadDescriptor extends S3UploadDescriptor {
    @SerializedName("awsAccessKey")
    private String awsAccessKey;

    @SerializedName("awsSecretKey")
    private String awsSecretKey;

    @SerializedName("sessionToken")
    private String sessionToken;

    @SerializedName("headers")
    private Map<String, String> headers = null;

    /**
     * Get awsAccessKey
     * @return awsAccessKey
     */
    public String getAwsAccessKey() {
        return awsAccessKey;
    }

    /**
     * Get awsSecretKey
     * @return awsSecretKey
     */
    public String getAwsSecretKey() {
        return awsSecretKey;
    }

    /**
     * Get sessionToken
     * @return sessionToken
     */
    public String getSessionToken() {
        return sessionToken;
    }

    /**
     * Get headers
     * @return headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

}
