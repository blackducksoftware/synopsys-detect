package com.synopsys.integration.detect.lifecycle.run.workflow;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.exception.IntegrationException;

public abstract class Workflow {
    private final OperationFactory operationFactory;

    public Workflow(OperationFactory operationFactory) {
        this.operationFactory = operationFactory;
    }

    public OperationFactory getOperationFactory() {
        return operationFactory;
    }

    public abstract WorkflowResult execute() throws DetectUserFriendlyException, IntegrationException;
}
