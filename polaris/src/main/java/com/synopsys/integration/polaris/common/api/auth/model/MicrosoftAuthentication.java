/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class MicrosoftAuthentication extends PolarisComponent {
    @SerializedName("enabled")
    private Boolean enabled;

    @SerializedName("allowSelfSignup")
    private Boolean allowSelfSignup;

    /**
     * When true, this will allow Users to authenticate using OAuth with Microsoft Live
     * @return enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(final Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get allowSelfSignup
     * @return allowSelfSignup
     */
    public Boolean getAllowSelfSignup() {
        return allowSelfSignup;
    }

    public void setAllowSelfSignup(final Boolean allowSelfSignup) {
        this.allowSelfSignup = allowSelfSignup;
    }

}

