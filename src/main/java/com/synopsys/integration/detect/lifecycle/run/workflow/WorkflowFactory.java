package com.synopsys.integration.detect.lifecycle.run.workflow;

import com.synopsys.integration.detect.lifecycle.run.RunContext;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckRunOptions;

public class WorkflowFactory {

    public Workflow createWorkflow(RunContext runContext) {
        OperationFactory operationFactory = new OperationFactory(runContext);
        BlackDuckRunOptions blackDuckRunOptions = runContext.createBlackDuckRunOptions();
        if (blackDuckRunOptions.shouldPerformRapidModeScan()) {
            return new RapidScanWorkflow(operationFactory);
        }
        return new DefaultWorkflow(operationFactory);
    }
}
