package com.synopsys.integration.detect.lifecycle.run.step.utility;

import java.io.IOException;
import java.util.function.Consumer;

import com.synopsys.integration.blackduck.exception.BlackDuckTimeoutExceededException;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeManager;
import com.synopsys.integration.detect.workflow.status.Operation;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.exception.IntegrationRestException;

public class OperationWrapper {
    private static final String OPERATION_ERROR_MESSAGE_PREFIX = "There was a problem:";
    private final ExitCodeManager exitCodeManager;

    public OperationWrapper(ExitCodeManager exitCodeManager) {
        this.exitCodeManager = exitCodeManager;
    }

    public void named(String name, Operation operation, OperationFunction supplier) throws DetectUserFriendlyException {
        named(name, operation, () -> { //To reduce duplication, calling the supplier with a return type but throwing away the returned result.
            supplier.execute();
            return true;
        });
    }

    public <T> T named(String name, Operation operation, OperationSupplier<T> supplier) throws DetectUserFriendlyException {
        return named(name, operation, supplier, () -> {}, (e) -> {});
    }

    public <T> T namedWithCallbacks(String name, Operation operation, OperationSupplier<T> supplier, Runnable successConsumer, Consumer<Exception> errorConsumer) throws DetectUserFriendlyException {
        return named(name, operation, supplier, successConsumer, errorConsumer);
    }

    public <T> T named(String name, Operation operation, OperationSupplier<T> supplier, Runnable successConsumer, Consumer<Exception> errorConsumer) throws DetectUserFriendlyException {
        try {
            T value = supplier.execute();
            operation.success();
            successConsumer.run();
            return value;
        } catch (InterruptedException e) {
            String errorReason = String.format("%s %s", OPERATION_ERROR_MESSAGE_PREFIX, e.getMessage().replace(OPERATION_ERROR_MESSAGE_PREFIX, ""));
            operation.error(name, errorReason);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            errorConsumer.accept(e);
            throw new DetectUserFriendlyException(errorReason, e, ExitCodeType.FAILURE_GENERAL_ERROR);
        } catch (Exception e) {
            String errorReason = String.format("%s %s", OPERATION_ERROR_MESSAGE_PREFIX, e.getMessage().replace(OPERATION_ERROR_MESSAGE_PREFIX, ""));
            operation.error(name, errorReason);
            errorConsumer.accept(e);
            throw new DetectUserFriendlyException(errorReason, e, exitCodeManager.getExitCodeFromExceptionDetails(e));
        } finally {
            operation.finish();
        }
    }

    @FunctionalInterface
    public interface OperationSupplier<T> {
        public T execute() throws DetectUserFriendlyException, IntegrationException, InterruptedException, IOException, IntegrationRestException, BlackDuckTimeoutExceededException; //basically all known detect exceptions.
    }

    @FunctionalInterface
    public interface OperationFunction {
        public void execute() throws DetectUserFriendlyException, IntegrationException, InterruptedException, IOException, IntegrationRestException, BlackDuckTimeoutExceededException; //basically all known detect exceptions.
    }
}
