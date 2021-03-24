/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.job.model;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class S3PreSignedDirectoryUploadDescriptor extends S3UploadDescriptor {
    @SerializedName("headers")
    private Map<String, String> headers = null;

    @SerializedName("preSignedURLsGenerated")
    private Boolean preSignedURLsGenerated;

    @SerializedName("preSignedURLUploadDescriptors")
    private List<PreSignedURLUploadDescriptor> preSignedURLUploadDescriptors = null;

    /**
     * Get headers
     * @return headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Get preSignedURLsGenerated
     * @return preSignedURLsGenerated
     */
    public Boolean getPreSignedURLsGenerated() {
        return preSignedURLsGenerated;
    }

    public void setPreSignedURLsGenerated(Boolean preSignedURLsGenerated) {
        this.preSignedURLsGenerated = preSignedURLsGenerated;
    }

    /**
     * Get preSignedURLUploadDescriptors
     * @return preSignedURLUploadDescriptors
     */
    public List<PreSignedURLUploadDescriptor> getPreSignedURLUploadDescriptors() {
        return preSignedURLUploadDescriptors;
    }

}
