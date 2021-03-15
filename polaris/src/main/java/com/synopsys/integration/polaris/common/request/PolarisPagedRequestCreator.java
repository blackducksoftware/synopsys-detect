/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.request;

import java.util.function.BiFunction;

import com.synopsys.integration.rest.request.Request;

public interface PolarisPagedRequestCreator extends BiFunction<Integer, Integer, Request> {
}
