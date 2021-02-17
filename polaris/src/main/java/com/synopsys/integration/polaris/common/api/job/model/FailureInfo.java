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
