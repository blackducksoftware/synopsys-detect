package com.synopsys.integration.detect.workflow.status;

import com.synopsys.integration.blackduck.codelocation.Result;

public class SignatureScanStatus extends Status {
    public SignatureScanStatus(String scanTargetPath, Result result) {
        this(scanTargetPath, result == Result.SUCCESS ? StatusType.SUCCESS : StatusType.FAILURE);
    }

    public SignatureScanStatus(String scanTargetPath, StatusType statusType) {
        super("Signature scan / Snippet scan on " + scanTargetPath, statusType);
    }
}
