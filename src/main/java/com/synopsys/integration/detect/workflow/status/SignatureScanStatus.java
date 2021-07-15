/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.status;

import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;

public class SignatureScanStatus extends Status {
    public SignatureScanStatus(String scanTargetPath, Result result) {
        this(scanTargetPath, result == Result.SUCCESS ? StatusType.SUCCESS : StatusType.FAILURE);
    }

    public SignatureScanStatus(String scanTargetPath, StatusType statusType) {
        super("Signature scan / Snippet scan on " + scanTargetPath, DetectTool.SIGNATURE_SCAN, statusType);
    }
}
