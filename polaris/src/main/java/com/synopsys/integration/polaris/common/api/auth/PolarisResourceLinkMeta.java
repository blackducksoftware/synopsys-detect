/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

public class PolarisResourceLinkMeta extends PolarisComponent {
    private String durable;

    public String getDurable() {
        return durable;
    }

    public void setDurable(final String durable) {
        this.durable = durable;
    }

}
