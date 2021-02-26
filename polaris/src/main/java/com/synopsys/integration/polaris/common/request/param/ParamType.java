/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.request.param;

public enum ParamType implements ParamEnum {
    FILTER("filter"),
    INCLUDE("include"),
    PAGE("page");

    private final String key;

    ParamType(final String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }

}
