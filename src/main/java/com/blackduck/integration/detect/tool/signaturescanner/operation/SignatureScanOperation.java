package com.blackduck.integration.detect.tool.signaturescanner.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackduck.integration.blackduck.codelocation.signaturescanner.ScanBatch;
import com.blackduck.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.blackduck.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.blackduck.integration.exception.IntegrationException;

public class SignatureScanOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SignatureScanOuputResult performScanActions(ScanBatch scanJob, ScanBatchRunner scanJobManager) throws IntegrationException {
        ScanBatchOutput scanJobOutput = scanJobManager.executeScans(scanJob);
        return new SignatureScanOuputResult(scanJobOutput);
    }
}
