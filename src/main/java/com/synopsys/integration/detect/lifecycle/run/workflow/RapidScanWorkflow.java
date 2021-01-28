package com.synopsys.integration.detect.lifecycle.run.workflow;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.RunResult;
import com.synopsys.integration.detect.lifecycle.run.operation.BlackDuckOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.DetectorOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationResult;
import com.synopsys.integration.exception.IntegrationException;

public class RapidScanWorkflow extends Workflow {

    public RapidScanWorkflow(OperationFactory operationFactory) {
        super(operationFactory);
    }

    @Override
    public WorkflowResult execute() throws DetectUserFriendlyException, IntegrationException {
        RunResult runResult = new RunResult();
        DetectorOperation detectorOperation = getOperationFactory().createDetectorOperation();
        OperationResult<RunResult> detectorResult = detectorOperation.execute(runResult);
        BlackDuckOperation blackDuckOperation = getOperationFactory().createRapidScanOperation(detectorResult.hasSucceeded());
        blackDuckOperation.execute(runResult);
        return null;
    }
}
