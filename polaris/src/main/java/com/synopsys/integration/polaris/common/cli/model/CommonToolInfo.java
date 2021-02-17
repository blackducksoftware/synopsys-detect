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
package com.synopsys.integration.polaris.common.cli.model;

import java.util.Optional;

public class CommonToolInfo {
    private String toolName;
    private String toolVersion;
    private String jobId;
    private String jobStatusUrl;
    private String jobStatus;
    private String issueApiUrl;

    public String getToolName() {
        return toolName;
    }

    public void setToolName(final String toolName) {
        this.toolName = toolName;
    }

    public String getToolVersion() {
        return toolVersion;
    }

    public void setToolVersion(final String toolVersion) {
        this.toolVersion = toolVersion;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(final String jobId) {
        this.jobId = jobId;
    }

    public String getJobStatusUrl() {
        return jobStatusUrl;
    }

    public void setJobStatusUrl(final String jobStatusUrl) {
        this.jobStatusUrl = jobStatusUrl;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(final String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Optional<String> getIssueApiUrl() {
        return Optional.ofNullable(issueApiUrl);
    }

    public void setIssueApiUrl(final String issueApiUrl) {
        this.issueApiUrl = issueApiUrl;
    }

}
