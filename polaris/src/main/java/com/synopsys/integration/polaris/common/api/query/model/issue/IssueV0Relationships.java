/**
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
package com.synopsys.integration.polaris.common.api.query.model.issue;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisRelationshipSingle;
import com.synopsys.integration.polaris.common.api.PolarisRelationships;

public class IssueV0Relationships extends PolarisRelationships {
    @SerializedName("issue-type")
    private PolarisRelationshipSingle issueType = null;

    @SerializedName("tool-domain-service")
    private PolarisRelationshipSingle toolDomainService = null;

    @SerializedName("tool")
    private PolarisRelationshipSingle tool = null;

    @SerializedName("path")
    private PolarisRelationshipSingle path = null;

    @SerializedName("latest-observed-on-run")
    private PolarisRelationshipSingle latestObservedOnRun = null;

    @SerializedName("transitions")
    private List<PolarisRelationshipSingle> transitions = null;

    @SerializedName("related-taxa")
    private List<PolarisRelationshipSingle> relatedTaxa = null;

    @SerializedName("related-indicators")
    private List<PolarisRelationshipSingle> relatedIndicators = null;

    /**
     * Get issueType
     * @return issueType
     */
    public PolarisRelationshipSingle getIssueType() {
        return issueType;
    }

    public void setIssueType(final PolarisRelationshipSingle issueType) {
        this.issueType = issueType;
    }

    /**
     * Get toolDomainService
     * @return toolDomainService
     */
    public PolarisRelationshipSingle getToolDomainService() {
        return toolDomainService;
    }

    public void setToolDomainService(final PolarisRelationshipSingle toolDomainService) {
        this.toolDomainService = toolDomainService;
    }

    /**
     * Get tool
     * @return tool
     */
    public PolarisRelationshipSingle getTool() {
        return tool;
    }

    public void setTool(final PolarisRelationshipSingle tool) {
        this.tool = tool;
    }

    /**
     * Get path
     * @return path
     */
    public PolarisRelationshipSingle getPath() {
        return path;
    }

    public void setPath(final PolarisRelationshipSingle path) {
        this.path = path;
    }

    /**
     * Get latestObservedOnRun
     * @return latestObservedOnRun
     */
    public PolarisRelationshipSingle getLatestObservedOnRun() {
        return latestObservedOnRun;
    }

    public void setLatestObservedOnRun(final PolarisRelationshipSingle latestObservedOnRun) {
        this.latestObservedOnRun = latestObservedOnRun;
    }

    /**
     * Get transitions
     * @return transitions
     */
    public List<PolarisRelationshipSingle> getTransitions() {
        return transitions;
    }

    public void setTransitions(final List<PolarisRelationshipSingle> transitions) {
        this.transitions = transitions;
    }

    /**
     * Get relatedTaxa
     * @return relatedTaxa
     */
    public List<PolarisRelationshipSingle> getRelatedTaxa() {
        return relatedTaxa;
    }

    public void setRelatedTaxa(final List<PolarisRelationshipSingle> relatedTaxa) {
        this.relatedTaxa = relatedTaxa;
    }

    /**
     * Get relatedIndicators
     * @return relatedIndicators
     */
    public List<PolarisRelationshipSingle> getRelatedIndicators() {
        return relatedIndicators;
    }

    public void setRelatedIndicators(final List<PolarisRelationshipSingle> relatedIndicators) {
        this.relatedIndicators = relatedIndicators;
    }

}

