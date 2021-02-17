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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisAttributes;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class JobAttributes extends PolarisAttributes {
    @SerializedName("analysis_runtime_info")
    private Map<String, Object> analysisRuntimeInfo = null;

    @SerializedName("branchName")
    private String branchName;

    @SerializedName("dateCancelled")
    private String dateCancelled;

    @SerializedName("dateCompleted")
    private String dateCompleted;

    @SerializedName("dateCreated")
    private String dateCreated;

    @SerializedName("dateFailed")
    private String dateFailed;

    @SerializedName("dateFinished")
    private String dateFinished;

    @SerializedName("dateQueued")
    private String dateQueued;

    @SerializedName("dateStarted")
    private String dateStarted;

    @SerializedName("details")
    private Map<String, Object> details = null;

    @SerializedName("diagnosticsKey")
    private String diagnosticsKey;

    @SerializedName("expiryDate")
    private String expiryDate;

    @SerializedName("failureInfo")
    private FailureInfo failureInfo = null;

    @SerializedName("intermediateDirectoryMD5")
    private String intermediateDirectoryMD5;

    @SerializedName("intermediateDirectoryStorageKey")
    private String intermediateDirectoryStorageKey;

    @SerializedName("intermediateDirectoryUploadDescriptor")
    private S3UploadDescriptor intermediateDirectoryUploadDescriptor = null;

    @SerializedName("jobType")
    private String jobType;

    @SerializedName("lifecycleEvents")
    private List<LifecycleEvent> lifecycleEvents = null;

    @SerializedName("lifecyclePhases")
    private List<LifecyclePhase> lifecyclePhases = null;

    @SerializedName("priority")
    private Integer priority;

    @SerializedName("projectName")
    private String projectName;

    @SerializedName("resultsKey")
    private String resultsKey;

    @SerializedName("revisionName")
    private String revisionName;

    @SerializedName("status")
    private JobStatus status = null;

    @SerializedName("submission")
    private Submission submission = null;

    @SerializedName("swip_spi_metadata")
    private Map<String, Object> swipSpiMetadata = null;

    public JobAttributes putAnalysisRuntimeInfoItem(String key, Object analysisRuntimeInfoItem) {
        if (this.analysisRuntimeInfo == null) {
            this.analysisRuntimeInfo = new HashMap<>();
        }
        this.analysisRuntimeInfo.put(key, analysisRuntimeInfoItem);
        return this;
    }

    /**
     * Get analysisRuntimeInfo
     * @return analysisRuntimeInfo
     */
    public Map<String, Object> getAnalysisRuntimeInfo() {
        return analysisRuntimeInfo;
    }

    public void setAnalysisRuntimeInfo(Map<String, Object> analysisRuntimeInfo) {
        this.analysisRuntimeInfo = analysisRuntimeInfo;
    }

    /**
     * Get branchName
     * @return branchName
     */
    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    /**
     * Get dateCancelled
     * @return dateCancelled
     */
    public String getDateCancelled() {
        return dateCancelled;
    }

    public void setDateCancelled(String dateCancelled) {
        this.dateCancelled = dateCancelled;
    }

    /**
     * Get dateCompleted
     * @return dateCompleted
     */
    public String getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(String dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    /**
     * Get dateCreated
     * @return dateCreated
     */
    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * Get dateFailed
     * @return dateFailed
     */
    public String getDateFailed() {
        return dateFailed;
    }

    public void setDateFailed(String dateFailed) {
        this.dateFailed = dateFailed;
    }

    /**
     * Get dateFinished
     * @return dateFinished
     */
    public String getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(String dateFinished) {
        this.dateFinished = dateFinished;
    }

    /**
     * Get dateQueued
     * @return dateQueued
     */
    public String getDateQueued() {
        return dateQueued;
    }

    public void setDateQueued(String dateQueued) {
        this.dateQueued = dateQueued;
    }

    /**
     * Get dateStarted
     * @return dateStarted
     */
    public String getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(String dateStarted) {
        this.dateStarted = dateStarted;
    }

    public JobAttributes putDetailsItem(String key, Object detailsItem) {
        if (this.details == null) {
            this.details = new HashMap<>();
        }
        this.details.put(key, detailsItem);
        return this;
    }

    /**
     * Get details
     * @return details
     */
    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    /**
     * Get diagnosticsKey
     * @return diagnosticsKey
     */
    public String getDiagnosticsKey() {
        return diagnosticsKey;
    }

    public void setDiagnosticsKey(String diagnosticsKey) {
        this.diagnosticsKey = diagnosticsKey;
    }

    /**
     * Get expiryDate
     * @return expiryDate
     */
    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Get failureInfo
     * @return failureInfo
     */
    public FailureInfo getFailureInfo() {
        return failureInfo;
    }

    public void setFailureInfo(FailureInfo failureInfo) {
        this.failureInfo = failureInfo;
    }

    /**
     * Get intermediateDirectoryMD5
     * @return intermediateDirectoryMD5
     */
    public String getIntermediateDirectoryMD5() {
        return intermediateDirectoryMD5;
    }

    public void setIntermediateDirectoryMD5(String intermediateDirectoryMD5) {
        this.intermediateDirectoryMD5 = intermediateDirectoryMD5;
    }

    /**
     * Get intermediateDirectoryStorageKey
     * @return intermediateDirectoryStorageKey
     */
    public String getIntermediateDirectoryStorageKey() {
        return intermediateDirectoryStorageKey;
    }

    public void setIntermediateDirectoryStorageKey(String intermediateDirectoryStorageKey) {
        this.intermediateDirectoryStorageKey = intermediateDirectoryStorageKey;
    }

    /**
     * Get intermediateDirectoryUploadDescriptor
     * @return intermediateDirectoryUploadDescriptor
     */
    public S3UploadDescriptor getIntermediateDirectoryUploadDescriptor() {
        return intermediateDirectoryUploadDescriptor;
    }

    public void setIntermediateDirectoryUploadDescriptor(S3UploadDescriptor intermediateDirectoryUploadDescriptor) {
        this.intermediateDirectoryUploadDescriptor = intermediateDirectoryUploadDescriptor;
    }

    /**
     * Get jobType
     * @return jobType
     */
    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public JobAttributes addLifecycleEventsItem(LifecycleEvent lifecycleEventsItem) {
        if (this.lifecycleEvents == null) {
            this.lifecycleEvents = new ArrayList<>();
        }
        this.lifecycleEvents.add(lifecycleEventsItem);
        return this;
    }

    /**
     * Get lifecycleEvents
     * @return lifecycleEvents
     */
    public List<LifecycleEvent> getLifecycleEvents() {
        return lifecycleEvents;
    }

    public void setLifecycleEvents(List<LifecycleEvent> lifecycleEvents) {
        this.lifecycleEvents = lifecycleEvents;
    }

    public JobAttributes addLifecyclePhasesItem(LifecyclePhase lifecyclePhasesItem) {
        if (this.lifecyclePhases == null) {
            this.lifecyclePhases = new ArrayList<>();
        }
        this.lifecyclePhases.add(lifecyclePhasesItem);
        return this;
    }

    /**
     * Get lifecyclePhases
     * @return lifecyclePhases
     */
    public List<LifecyclePhase> getLifecyclePhases() {
        return lifecyclePhases;
    }

    public void setLifecyclePhases(List<LifecyclePhase> lifecyclePhases) {
        this.lifecyclePhases = lifecyclePhases;
    }

    /**
     * Get priority
     * @return priority
     */
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * Get projectName
     * @return projectName
     */
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Get resultsKey
     * @return resultsKey
     */
    public String getResultsKey() {
        return resultsKey;
    }

    public void setResultsKey(String resultsKey) {
        this.resultsKey = resultsKey;
    }

    /**
     * Get revisionName
     * @return revisionName
     */
    public String getRevisionName() {
        return revisionName;
    }

    public void setRevisionName(String revisionName) {
        this.revisionName = revisionName;
    }

    /**
     * Get status
     * @return status
     */
    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    /**
     * Get submission
     * @return submission
     */
    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public JobAttributes putSwipSpiMetadataItem(String key, Object swipSpiMetadataItem) {
        if (this.swipSpiMetadata == null) {
            this.swipSpiMetadata = new HashMap<>();
        }
        this.swipSpiMetadata.put(key, swipSpiMetadataItem);
        return this;
    }

    /**
     * Get swipSpiMetadata
     * @return swipSpiMetadata
     */
    public Map<String, Object> getSwipSpiMetadata() {
        return swipSpiMetadata;
    }

    public void setSwipSpiMetadata(Map<String, Object> swipSpiMetadata) {
        this.swipSpiMetadata = swipSpiMetadata;
    }

}
