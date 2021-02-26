/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model.user;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisAttributes;

public class EmailDetailsAttributes extends PolarisAttributes {
    @SerializedName("email-verified")
    private Boolean emailVerified;
    @SerializedName("email")
    private String email;

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmailVerified(final Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

}
