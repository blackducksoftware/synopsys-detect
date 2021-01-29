/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.lifecycle.run.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.exception.IntegrationException;

public abstract class Operation<I, T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract boolean shouldExecute();

    public abstract String getOperationName();

    protected abstract OperationResult<T> executeOperation(I input) throws DetectUserFriendlyException, IntegrationException;

    protected void logOperationStarted() {
        logger.debug("Operation {} started.", getOperationName());
    }

    protected void logOperationFinished() {
        logger.debug("Operation {} finished.", getOperationName());
    }

    protected void logOperationSkipped() {
        logger.debug("Operation {} skipped.", getOperationName());
    }

    public final OperationResult<T> execute(I input) throws DetectUserFriendlyException, IntegrationException {
        //TODO figure out how to differentiate the output and the input correctly
        OperationResult<T> result = OperationResult.success();
        if (shouldExecute()) {
            logOperationStarted();
            result = executeOperation(input);
            logOperationFinished();
        } else {
            logOperationSkipped();
        }
        return result;
    }
}
