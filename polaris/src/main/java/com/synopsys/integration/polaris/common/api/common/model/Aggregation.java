/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.common.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class Aggregation extends PolarisComponent {
    @SerializedName("group")
    private List<String> group = null;

    @SerializedName("type")
    private String type;

    @SerializedName("when")
    private String when;

    @SerializedName("results")
    private List<AggregationGroup> results = null;

    public Aggregation addGroupItem(final String groupItem) {
        if (this.group == null) {
            this.group = new ArrayList<>();
        }
        this.group.add(groupItem);
        return this;
    }

    /**
     * Get group
     * @return group
     */
    public List<String> getGroup() {
        return group;
    }

    public void setGroup(final List<String> group) {
        this.group = group;
    }

    /**
     * Get type
     * @return type
     */
    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Get when
     * @return when
     */
    public String getWhen() {
        return when;
    }

    public void setWhen(final String when) {
        this.when = when;
    }

    public Aggregation addResultsItem(final AggregationGroup resultsItem) {
        if (this.results == null) {
            this.results = new ArrayList<>();
        }
        this.results.add(resultsItem);
        return this;
    }

    /**
     * Get results
     * @return results
     */
    public List<AggregationGroup> getResults() {
        return results;
    }

    public void setResults(final List<AggregationGroup> results) {
        this.results = results;
    }

}

