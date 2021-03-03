/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.query.model.issue.type;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisAttributes;

public class IssueTypeV0Attributes extends PolarisAttributes {
    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("issue-type")
    private String issueType;

    @SerializedName("abbreviation")
    private String abbreviation;

    @SerializedName("local-effect")
    private String localEffect;

    /**
     * Localized name of the issue-type.
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Localized description of the issue-type.
     * @return description
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * The issue-type semantic id.
     * @return issueType
     */
    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(final String issueType) {
        this.issueType = issueType;
    }

    /**
     * Localized abbreviation of the issue-type.
     * @return abbreviation
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(final String abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**
     * Localized local effect of the issue-type
     * @return localEffect
     */
    public String getLocalEffect() {
        return localEffect;
    }

    public void setLocalEffect(final String localEffect) {
        this.localEffect = localEffect;
    }

}

