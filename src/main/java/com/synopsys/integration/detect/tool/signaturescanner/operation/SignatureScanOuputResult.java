package com.synopsys.integration.detect.tool.signaturescanner.operation;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;

public class SignatureScanOuputResult {
    private final ScanBatchOutput scanBatchOutput;

    public SignatureScanOuputResult(ScanBatchOutput scanBatchOutput) {
        this.scanBatchOutput = scanBatchOutput;
    }

    public ScanBatchOutput getScanBatchOutput() {
        return scanBatchOutput;
    }
}
