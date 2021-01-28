package com.synopsys.integration.detect.lifecycle.run.workflow;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.RunResult;
import com.synopsys.integration.detect.lifecycle.run.operation.BazelOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.BlackDuckOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.DetectorOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.DockerOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationResult;
import com.synopsys.integration.detect.lifecycle.run.operation.PolarisOperation;
import com.synopsys.integration.exception.IntegrationException;

public class DefaultWorkflow extends Workflow {
    public DefaultWorkflow(OperationFactory operationFactory) {
        super(operationFactory);
    }

    @Override
    public WorkflowResult execute() throws DetectUserFriendlyException, IntegrationException {
        RunResult runResult = new RunResult();
        PolarisOperation polarisOperation = getOperationFactory().createPolarisOperation();
        DockerOperation dockerOperation = getOperationFactory().createDockerOperation();
        BazelOperation bazelOperation = getOperationFactory().createBazelOperation();
        DetectorOperation detectorOperation = getOperationFactory().createDetectorOperation();

        polarisOperation.execute(null);
        OperationResult<RunResult> dockerResult = dockerOperation.execute(runResult);
        OperationResult<RunResult> bazelResult = bazelOperation.execute(runResult);
        OperationResult<RunResult> detectorResult = detectorOperation.execute(runResult);

        boolean priorOperationsSucceeded = dockerResult.hasSucceeded() && bazelResult.hasSucceeded() && detectorResult.hasSucceeded();

        BlackDuckOperation blackDuckOperation = getOperationFactory().createFullScanOperation(priorOperationsSucceeded);
        OperationResult<RunResult> blackDuckResult = blackDuckOperation.execute(runResult);
        return null;
    }
}
