/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.request;

import java.lang.reflect.Type;

public class PolarisPagedRequestWrapper {
    private final PolarisPagedRequestCreator requestCreator;
    private final Type type;

    public PolarisPagedRequestWrapper(final PolarisPagedRequestCreator requestCreator, final Type type) {
        this.requestCreator = requestCreator;
        this.type = type;
    }

    public PolarisPagedRequestCreator getRequestCreator() {
        return requestCreator;
    }

    public Type getResponseType() {
        return type;
    }

}
