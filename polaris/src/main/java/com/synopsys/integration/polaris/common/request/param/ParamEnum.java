/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.request.param;

public interface ParamEnum {
    String getKey();

    default boolean equalsKey(final String candidate) {
        return getKey().equals(candidate);
    }

}
