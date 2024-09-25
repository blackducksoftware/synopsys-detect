package com.blackduck.integration.detect.tool.signaturescanner.operation;

import com.blackduck.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;

public class SignatureScanOuputResult {
    private final ScanBatchOutput scanBatchOutput;

    public SignatureScanOuputResult(ScanBatchOutput scanBatchOutput) {
        this.scanBatchOutput = scanBatchOutput;
    }

    public ScanBatchOutput getScanBatchOutput() {
        return scanBatchOutput;
    }
}
