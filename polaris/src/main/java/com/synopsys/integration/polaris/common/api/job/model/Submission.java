/**
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

public class Submission extends PolarisComponent {
    @SerializedName("submissionDataMD5")
    private String submissionDataMD5;

    @SerializedName("submissionData")
    private String submissionData;

    @SerializedName("submissionUploadDescriptor")
    private S3UploadDescriptor submissionUploadDescriptor = null;

    @SerializedName("uploadDescriptorType")
    private String uploadDescriptorType;

    @SerializedName("submissionFlavor")
    private SubmissionFlavor submissionFlavor = null;

    @SerializedName("fingerprint")
    private String fingerprint;

    /**
     * Get submissionDataMD5
     * @return submissionDataMD5
     */
    public String getSubmissionDataMD5() {
        return submissionDataMD5;
    }

    /**
     * Get submissionData
     * @return submissionData
     */
    public String getSubmissionData() {
        return submissionData;
    }

    /**
     * Get submissionUploadDescriptor
     * @return submissionUploadDescriptor
     */
    public S3UploadDescriptor getSubmissionUploadDescriptor() {
        return submissionUploadDescriptor;
    }

    public void setSubmissionUploadDescriptor(S3UploadDescriptor submissionUploadDescriptor) {
        this.submissionUploadDescriptor = submissionUploadDescriptor;
    }

    /**
     * Get uploadDescriptorType
     * @return uploadDescriptorType
     */
    public String getUploadDescriptorType() {
        return uploadDescriptorType;
    }

    /**
     * Get submissionFlavor
     * @return submissionFlavor
     */
    public SubmissionFlavor getSubmissionFlavor() {
        return submissionFlavor;
    }

    public void setSubmissionFlavor(SubmissionFlavor submissionFlavor) {
        this.submissionFlavor = submissionFlavor;
    }

    /**
     * Get fingerprint
     * @return fingerprint
     */
    public String getFingerprint() {
        return fingerprint;
    }

}
