/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api;

import java.util.Arrays;
import java.util.List;

public class PolarisRelationshipMultiple extends PolarisRelationship {
    private List<PolarisResourceSparse> data;

    public List<PolarisResourceSparse> getData() {
        if (null != data) {
            return data;
        }
        return Arrays.asList();
    }

    public void setData(final List<PolarisResourceSparse> data) {
        this.data = data;
    }

}
