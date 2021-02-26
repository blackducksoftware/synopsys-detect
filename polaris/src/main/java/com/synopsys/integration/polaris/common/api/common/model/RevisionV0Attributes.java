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

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class RevisionV0Attributes extends PolarisComponent {
    @SerializedName("name")
    private String name;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("modified-off-the-record")
    private Boolean modifiedOffTheRecord = false;

    /**
     * &#x60;Mutable&#x60;
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * The time when the revision is created.
     * @return timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * A Revision is &#x60;modified-off-the-record&#x60; when it doesn&#39;t exist in the Scm.  Most Revisions come directly from the Scm, hence they aren&#39;t &#x60;modified-off-the-record&#x60;.
     * @return modifiedOffTheRecord
     */
    public Boolean getModifiedOffTheRecord() {
        return modifiedOffTheRecord;
    }

    public void setModifiedOffTheRecord(final Boolean modifiedOffTheRecord) {
        this.modifiedOffTheRecord = modifiedOffTheRecord;
    }

}

