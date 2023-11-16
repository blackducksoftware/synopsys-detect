package com.synopsys.integration.detect.lifecycle.run.step;

import java.util.Optional;
import java.util.UUID;

import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;

public class RlScanStepRunner {

    private final OperationRunner operationRunner;
    
    public RlScanStepRunner(OperationRunner operationRunner) {
        this.operationRunner = operationRunner;
    }

    public Optional<UUID> invokeRlWorkflow() {
        // TODO Auto-generated method stub
        return null;
    }

}
