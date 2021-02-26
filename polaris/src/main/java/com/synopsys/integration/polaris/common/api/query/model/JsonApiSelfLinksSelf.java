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

public class JsonApiSelfLinksSelf extends PolarisComponent {
    @SerializedName("meta")
    private JsonApiSelfLinksSelfMeta meta = null;

    @SerializedName("href")
    private String href;

    /**
     * Get meta
     * @return meta
     */
    public JsonApiSelfLinksSelfMeta getMeta() {
        return meta;
    }

    public void setMeta(final JsonApiSelfLinksSelfMeta meta) {
        this.meta = meta;
    }

    /**
     * Get href
     * @return href
     */
    public String getHref() {
        return href;
    }

    public void setHref(final String href) {
        this.href = href;
    }

}

