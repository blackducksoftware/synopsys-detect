/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

public class PolarisResourceLinks extends PolarisComponent {
    @SerializedName("self")
    private PolarisResourceLink self;

    public PolarisResourceLink getSelf() {
        return self;
    }

    public void setSelf(final PolarisResourceLink self) {
        this.self = self;
    }

}
