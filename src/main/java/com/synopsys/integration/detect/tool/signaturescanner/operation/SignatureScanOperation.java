package com.synopsys.integration.detect.tool.signaturescanner.operation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatch;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandOutput;
import com.synopsys.integration.exception.IntegrationException;

public class SignatureScanOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SignatureScanOuputResult performScanActions(ScanBatch scanJob, ScanBatchRunner scanJobManager) throws IntegrationException {
        List<ScanCommandOutput> scanCommandOutputs = new ArrayList<>();
        ScanBatchOutput scanJobOutput = scanJobManager.executeScans(scanJob);
        if (scanJobOutput.getOutputs() != null) {
            scanCommandOutputs.addAll(scanJobOutput.getOutputs());
        }

        return new SignatureScanOuputResult(scanCommandOutputs, scanJobOutput);
    }
}
