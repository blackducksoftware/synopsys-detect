/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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
