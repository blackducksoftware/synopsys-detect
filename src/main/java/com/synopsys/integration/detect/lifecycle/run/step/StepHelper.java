package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.exception.BlackDuckTimeoutExceededException;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.status.Operation;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.exception.IntegrationRestException;

public class StepHelper {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final OperationSystem operationSystem;
    private final DetectToolFilter detectToolFilter;

    public StepHelper(final OperationSystem operationSystem, DetectToolFilter detectToolFilter) {
        this.operationSystem = operationSystem;
        this.detectToolFilter = detectToolFilter;
    }

    public void runToolIfIncluded(DetectTool detectTool, String name, OperationFunction supplier) throws DetectUserFriendlyException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(detectTool)) {
            logger.info("Will include the " + name + " tool.");
            runAsGroup(name, supplier);
            logger.info(name + " actions finished.");
        } else {
            logger.info(name + " tool will not be run.");
        }
    }

    public <T> Optional<T> runToolIfIncluded(DetectTool detectTool, String name, OperationSupplier<T> supplier) throws DetectUserFriendlyException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(detectTool)) {
            logger.info("Will include the " + name + " tool.");
            Optional<T> value = Optional.ofNullable(runAsGroup(name, supplier));
            logger.info(name + " actions finished.");
            return value;
        } else {
            logger.info(name + " tool will not be run.");
            return Optional.empty();
        }
    }

    public void runAsGroup(String name, OperationFunction supplier) throws DetectUserFriendlyException {
        Operation operation = operationSystem.startOperation(name);
        try {
            supplier.execute();
            operationSystem.completeWithSuccess(name);
        } catch (DetectUserFriendlyException e) {
            operationSystem.completeWithError(name, e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            String errorReason = String.format("Your Black Duck configuration is not valid: %s", e.getMessage());
            operation.error(errorReason);
            throw new DetectUserFriendlyException(errorReason, e, ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
        } catch (IntegrationRestException e) {
            operation.error(e.getMessage());
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
        } catch (BlackDuckTimeoutExceededException e) {
            operation.error(e.getMessage());
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_TIMEOUT);
        } catch (InterruptedException e) {
            String errorReason = String.format("There was a problem: %s", e.getMessage());
            operation.error(errorReason);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            throw new DetectUserFriendlyException(errorReason, e, ExitCodeType.FAILURE_GENERAL_ERROR);
        } catch (Exception e) {
            String errorReason = String.format("There was a problem: %s", e.getMessage());
            operation.error(errorReason);
            throw new DetectUserFriendlyException(errorReason, e, ExitCodeType.FAILURE_GENERAL_ERROR);
        } finally {
            operation.finish();
        }
    }

    public <T> T runAsGroup(String name, OperationSupplier<T> supplier) throws DetectUserFriendlyException {
        Operation operation = operationSystem.startOperation(name);
        try {
            T value = supplier.execute();
            operationSystem.completeWithSuccess(name);
            return value;
        } catch (DetectUserFriendlyException e) {
            operationSystem.completeWithError(name, e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            String errorReason = String.format("Your Black Duck configuration is not valid: %s", e.getMessage());
            operation.error(errorReason);
            throw new DetectUserFriendlyException(errorReason, e, ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
        } catch (IntegrationRestException e) {
            operation.error(e.getMessage());
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY);
        } catch (BlackDuckTimeoutExceededException e) {
            operation.error(e.getMessage());
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_TIMEOUT);
        } catch (InterruptedException e) {
            String errorReason = String.format("There was a problem: %s", e.getMessage());
            operation.error(errorReason);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            throw new DetectUserFriendlyException(errorReason, e, ExitCodeType.FAILURE_GENERAL_ERROR);
        } catch (Exception e) {
            String errorReason = String.format("There was a problem: %s", e.getMessage());
            operation.error(errorReason);
            throw new DetectUserFriendlyException(errorReason, e, ExitCodeType.FAILURE_GENERAL_ERROR);
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
