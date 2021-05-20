/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.IOException;

import com.synopsys.integration.blackduck.exception.BlackDuckTimeoutExceededException;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.status.Operation;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.exception.IntegrationRestException;

//Essentially an adapter for 'running an operation' and 'reporting the operation' in one step. Whether or not this is desired is TBD.
public class OperationAuditLog { //NoOpAuditLog
    private final OperationSystem operationSystem;

    public OperationAuditLog(final OperationSystem operationSystem) {
        this.operationSystem = operationSystem;
    }

    public void named(String name, OperationFunction supplier) throws DetectUserFriendlyException {
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

    public <T> T named(String name, OperationSupplier<T> supplier) throws DetectUserFriendlyException {
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
