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

    public ContainerScanStepRunner(OperationRunner operationRunner) throws DetectUserFriendlyException {
        this.operationRunner = operationRunner;
    }

    public UUID submitScan(BlackDuckRunData blackDuckRunData) throws IOException, IntegrationException {
//        File bdioHeaderFile = new File(Application.class.getResource("/test-inputs/bdio-header.pb").getPath()); // temporary
        DetectProtobufBdioUtil detectProtobufBdioUtil = new DetectProtobufBdioUtil(UUID.randomUUID().toString(), "CONTAINER");
        File bdioHeaderFile = detectProtobufBdioUtil.createProtobufBdioHeader();
        return operationRunner.uploadBdioHeaderToInitiateScan(blackDuckRunData, bdioHeaderFile);
    }

//    public void createProtobufBdioHeaderFile() throws IOException {
//        DNUProtobufBdioHeader DNUProtobufBdioHeader = new DNUProtobufBdioHeader();
//    }
}
