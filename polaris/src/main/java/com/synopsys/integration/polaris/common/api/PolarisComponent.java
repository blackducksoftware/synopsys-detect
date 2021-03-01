/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api;

import java.io.Serializable;

import com.synopsys.integration.util.Stringable;

public class PolarisComponent extends Stringable implements Serializable {
    private String json;

    public PolarisComponent() {
        this.json = null;
    }

    public PolarisComponent(final String json) {
        this.json = json;
    }

    public String getJson() {
        return json;
    }

    public void setJson(final String json) {
        this.json = json;
    }

}
