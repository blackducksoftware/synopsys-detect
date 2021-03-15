/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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

