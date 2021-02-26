/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.result;

public class ExceptionDetectableResult extends FailedDetectableResult {
    private final Exception exception;

    public ExceptionDetectableResult(final Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toDescription() {
        return "Exception occurred: " + exception.getMessage();
    }
}
