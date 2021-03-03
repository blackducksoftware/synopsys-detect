/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model.group;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisAttributes;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class GroupAttributes extends PolarisAttributes {
    @SerializedName("date-created")
    private String dateCreated;

    @SerializedName("groupname")
    private String groupname;

    /**
     * Get dateCreated
     * @return dateCreated
     */
    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(final String dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * Get groupname
     * @return groupname
     */
    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(final String groupname) {
        this.groupname = groupname;
    }

}

