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

import java.util.ArrayList;
import java.util.List;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class TermsOfUseAttributes extends PolarisComponent {
    @SerializedName("sha256")
    private List<byte[]> sha256 = new ArrayList<>();

    @SerializedName("text")
    private String text;

    public TermsOfUseAttributes addSha256Item(final byte[] sha256Item) {
        this.sha256.add(sha256Item);
        return this;
    }

    /**
     * Get sha256
     * @return sha256
     */
    public List<byte[]> getSha256() {
        return sha256;
    }

    public void setSha256(final List<byte[]> sha256) {
        this.sha256 = sha256;
    }

    /**
     * Get text
     * @return text
     */
    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

}

