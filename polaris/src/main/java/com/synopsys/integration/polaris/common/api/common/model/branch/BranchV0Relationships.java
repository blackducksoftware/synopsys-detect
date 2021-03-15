/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.common.model.branch;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisRelationship;
import com.synopsys.integration.polaris.common.api.PolarisRelationships;
import com.synopsys.integration.polaris.common.api.common.model.JsonApiLazyRelationship;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class BranchV0Relationships extends PolarisRelationships {
    @SerializedName("project")
    private PolarisRelationship project = null;

    @SerializedName("revisions")
    private JsonApiLazyRelationship revisions = null;

    /**
     * Get project
     * @return project
     */
    public PolarisRelationship getProject() {
        return project;
    }

    public void setProject(final PolarisRelationship project) {
        this.project = project;
    }

    /**
     * Get revisions
     * @return revisions
     */
    public JsonApiLazyRelationship getRevisions() {
        return revisions;
    }

    public void setRevisions(final JsonApiLazyRelationship revisions) {
        this.revisions = revisions;
    }

}

