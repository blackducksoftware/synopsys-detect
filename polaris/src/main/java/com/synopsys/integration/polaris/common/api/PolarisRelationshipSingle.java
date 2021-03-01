/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api;

import java.util.Optional;

public class PolarisRelationshipSingle extends PolarisRelationship {
    private PolarisResourceSparse data;

    public Optional<PolarisResourceSparse> getData() {
        return Optional.ofNullable(data);
    }

    public void setData(final PolarisResourceSparse data) {
        this.data = data;
    }

}
