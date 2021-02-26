/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.result;

import org.apache.commons.lang3.StringUtils;

public class PropertyInsufficientDetectableResult extends FailedDetectableResult {
    private final String message;

    public PropertyInsufficientDetectableResult() {
        this(null);
    }

    public PropertyInsufficientDetectableResult(final String message) {
        this.message = message;
    }

    @Override
    public String toDescription() {
        return "The properties are insufficient to run. " + StringUtils.trimToEmpty(message);
    }
}
