/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.query.model;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class JsonApiSelfLinks extends PolarisComponent {
    @SerializedName("self")
    private JsonApiSelfLinksSelf self = null;

    /**
     * Get self
     * @return self
     */
    public JsonApiSelfLinksSelf getSelf() {
        return self;
    }

    public void setSelf(final JsonApiSelfLinksSelf self) {
        this.self = self;
    }

}

