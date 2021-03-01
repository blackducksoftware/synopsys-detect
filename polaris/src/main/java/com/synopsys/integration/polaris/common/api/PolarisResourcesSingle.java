/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api;

import java.util.Optional;

import com.google.gson.annotations.SerializedName;

public class PolarisResourcesSingle<R extends PolarisResource> extends PolarisResponse {
    @SerializedName("data")
    private R data;

    public Optional<R> getData() {
        return Optional.ofNullable(data);
    }

    public void setData(final R data) {
        this.data = data;
    }

}
