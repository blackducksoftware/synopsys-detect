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
package com.synopsys.integration.detect.lifecycle.run;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.lifecycle.run.workflow.Workflow;
import com.synopsys.integration.detect.lifecycle.run.workflow.WorkflowFactory;
import com.synopsys.integration.detect.lifecycle.run.workflow.WorkflowResult;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeManager;
import com.synopsys.integration.detect.workflow.DetectRun;
import com.synopsys.integration.detect.workflow.event.EventSystem;

public class RunManager2 {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectRun detectRun;
    private final ExitCodeManager exitCodeManager;
    private final EventSystem eventSystem;

    public RunManager2(DetectRun detectRun, ExitCodeManager exitCodeManager, EventSystem eventSystem) {
        this.detectRun = detectRun;
        this.exitCodeManager = exitCodeManager;
        this.eventSystem = eventSystem;
    }

    public WorkflowResult run(RunContext runContext) {
        WorkflowFactory workflowFactory = new WorkflowFactory();
        logger.debug("Detect run begin: {}", detectRun.getRunId());
        Workflow workflow = workflowFactory.createWorkflow(runContext);
        WorkflowResult result = workflow.execute();

        if (result.hasFailed()) {
            result.getException().ifPresent(exitCodeManager::requestExitCode);
        }

        return result;
    }
}
