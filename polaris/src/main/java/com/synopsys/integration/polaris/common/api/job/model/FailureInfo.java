/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.job.model;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.util.Stringable;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class FailureInfo extends Stringable {
    @SerializedName("userFriendlyFailureReason")
    private String userFriendlyFailureReason;

    @SerializedName("internalFailureReason")
    private String internalFailureReason;

    @SerializedName("threadId")
    private Long threadId;

    @SerializedName("threadGroupName")
    private String threadGroupName;

    @SerializedName("threadName")
    private String threadName;

    @SerializedName("exception")
    private String exception;

    @SerializedName("failureStackTrace")
    private String failureStackTrace;

    @SerializedName("osProcessInfo")
    private OSProcessInfo osProcessInfo = null;

    /**
     * Get userFriendlyFailureReason
     * @return userFriendlyFailureReason
     */
    public String getUserFriendlyFailureReason() {
        return userFriendlyFailureReason;
    }

    /**
     * Get internalFailureReason
     * @return internalFailureReason
     */
    public String getInternalFailureReason() {
        return internalFailureReason;
    }

    /**
     * Get threadId
     * @return threadId
     */
    public Long getThreadId() {
        return threadId;
    }

    /**
     * Get threadGroupName
     * @return threadGroupName
     */
    public String getThreadGroupName() {
        return threadGroupName;
    }

    /**
     * Get threadName
     * @return threadName
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * Get exception
     * @return exception
     */
    public String getException() {
        return exception;
    }

    /**
     * Get failureStackTrace
     * @return failureStackTrace
     */
    public String getFailureStackTrace() {
        return failureStackTrace;
    }

    /**
     * Get osProcessInfo
     * @return osProcessInfo
     */
    public OSProcessInfo getOsProcessInfo() {
        return osProcessInfo;
    }

    public void setOsProcessInfo(OSProcessInfo osProcessInfo) {
        this.osProcessInfo = osProcessInfo;
    }

}
