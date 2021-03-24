/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.response;

import java.util.List;
import java.util.function.Function;

import com.synopsys.integration.polaris.common.api.PolarisResource;
import com.synopsys.integration.polaris.common.api.PolarisResources;
import com.synopsys.integration.polaris.common.api.PolarisResourcesPagination;

public class PolarisContainerResponseExtractor {
    private final Function<PolarisResources, List<PolarisResource>> getResponseList;
    private final Function<PolarisResources, PolarisResourcesPagination> getMetaFunction;

    public PolarisContainerResponseExtractor(final Function<PolarisResources, List<PolarisResource>> getResponseList, final Function<PolarisResources, PolarisResourcesPagination> getMetaFunction) {
        this.getResponseList = getResponseList;
        this.getMetaFunction = getMetaFunction;
    }

    public Function<PolarisResources, List<PolarisResource>> getGetResponseList() {
        return getResponseList;
    }

    public Function<PolarisResources, PolarisResourcesPagination> getGetMetaFunction() {
        return getMetaFunction;
    }

}
