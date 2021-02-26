/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.status;

import com.synopsys.integration.blackduck.codelocation.Result;

public class SignatureScanStatus extends Status {
    public SignatureScanStatus(final String scanTargetPath, final Result result) {
        this(scanTargetPath, result == Result.SUCCESS ? StatusType.SUCCESS : StatusType.FAILURE);
    }

    public SignatureScanStatus(final String scanTargetPath, final StatusType statusType) {
        super("Signature scan / Snippet scan on " + scanTargetPath, statusType);
    }
}
