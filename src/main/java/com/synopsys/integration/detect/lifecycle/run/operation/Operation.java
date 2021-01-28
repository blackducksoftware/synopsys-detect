package com.synopsys.integration.detect.lifecycle.run.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.exception.IntegrationException;

public abstract class Operation<I, T> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract boolean shouldExecute();

    public abstract String getOperationName();

    protected abstract OperationResult<T> executeOperation(I input) throws DetectUserFriendlyException, IntegrationException;

    public final OperationResult<T> execute(I input) throws DetectUserFriendlyException, IntegrationException {
        String operationName = getOperationName();
        //TODO figure out how to differentiate the output and the input correctly
        OperationResult<T> result = OperationResult.success();
        if (shouldExecute()) {
            logger.info("Will include the {} tool.", operationName);
            result = executeOperation(input);
            logger.info("{} actions finished.", operationName);
        } else {
            logger.info("{} tool will not be run.", operationName);
        }
        return result;
    }
}
