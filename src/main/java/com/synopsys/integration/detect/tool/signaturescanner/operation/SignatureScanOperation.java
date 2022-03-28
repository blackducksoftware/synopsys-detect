package com.synopsys.integration.detect.tool.signaturescanner.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatch;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.exception.IntegrationException;

public class SignatureScanOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SignatureScanOuputResult performScanActions(ScanBatch scanJob, ScanBatchRunner scanJobManager) throws IntegrationException {
        ScanBatchOutput scanJobOutput = scanJobManager.executeScans(scanJob);
        return new SignatureScanOuputResult(scanJobOutput);
    }
}
