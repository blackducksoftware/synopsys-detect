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
package com.synopsys.integration.detect.lifecycle.run.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.exception.IntegrationException;

public abstract class Workflow {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final PropertyConfiguration detectConfiguration;
    private final OperationFactory operationFactory;

    public Workflow(PropertyConfiguration detectConfiguration, OperationFactory operationFactory) {
        this.detectConfiguration = detectConfiguration;
        this.operationFactory = operationFactory;
    }

    public OperationFactory getOperationFactory() {
        return operationFactory;
    }

    protected abstract WorkflowResult executeWorkflow() throws DetectUserFriendlyException, IntegrationException;

    public final WorkflowResult execute() {
        WorkflowResult result;
        try {
            executeWorkflow();
            logger.info("All tools have finished.");
            logger.info(ReportConstants.RUN_SEPARATOR);
            logger.debug("Detect run completed.");
            result = WorkflowResult.success();
        } catch (Exception ex) {
            if (ex.getMessage() != null) {
                logger.error("Detect run failed: {}", ex.getMessage());
            } else {
                logger.error("Detect run failed: {}", ex.getClass().getSimpleName());
            }
            logger.debug("An exception was thrown during the detect run.", ex);
            result = WorkflowResult.fail(ex);
        }
        return result;
    }

    public PropertyConfiguration getDetectConfiguration() {
        return detectConfiguration;
    }
}
