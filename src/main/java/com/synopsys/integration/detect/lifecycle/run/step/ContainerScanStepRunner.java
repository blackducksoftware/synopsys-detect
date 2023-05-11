package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.util.bdio.protobuf.DetectProtobufBdioUtil;
import com.synopsys.integration.exception.IntegrationException;

public class ContainerScanStepRunner {

    private final OperationRunner operationRunner;
    private UUID scanId;

    public ContainerScanStepRunner(OperationRunner operationRunner) throws DetectUserFriendlyException {
        this.operationRunner = operationRunner;
    }

    public UUID initiateScan(BlackDuckRunData blackDuckRunData) throws IOException, IntegrationException {
        DetectProtobufBdioUtil detectProtobufBdioUtil = new DetectProtobufBdioUtil(UUID.randomUUID().toString(), "CONTAINER");
        File bdioHeaderFile = detectProtobufBdioUtil.createProtobufBdioHeader();
        scanId = operationRunner.uploadBdioHeaderToInitiateScan(blackDuckRunData, bdioHeaderFile);
        return scanId;
    }
}
