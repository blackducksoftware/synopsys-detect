package com.synopsys.integration.detect.tool.signaturescanner.operation;

import java.util.List;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandOutput;

public class SignatureScanOuputResult {
    private final List<ScanCommandOutput> scanCommandOutputs;
    private final ScanBatchOutput scanBatchOutput;

    public SignatureScanOuputResult(final List<ScanCommandOutput> scanCommandOutputs, final ScanBatchOutput scanBatchOutput) {
        this.scanCommandOutputs = scanCommandOutputs;
        this.scanBatchOutput = scanBatchOutput;
    }

    public List<ScanCommandOutput> getScanCommandOutputs() {
        return scanCommandOutputs;
    }

    public ScanBatchOutput getScanBatchOutput() {
        return scanBatchOutput;
    }
}
