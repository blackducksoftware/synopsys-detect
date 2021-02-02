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
package com.synopsys.integration.detect.lifecycle.run.operation.blackduck;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.RunOptions;
import com.synopsys.integration.detect.lifecycle.run.operation.Operation;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationResult;
import com.synopsys.integration.detect.lifecycle.run.operation.input.BdioInput;
import com.synopsys.integration.detect.workflow.bdio.BdioManager;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.exception.IntegrationException;

public class BdioFileGenerationOperation extends Operation<BdioInput, BdioResult> {
    private final RunOptions runOptions;
    private final BdioOptions bdioOptions;
    private final BdioManager bdioManager;
    private final EventSystem eventSystem;

    public BdioFileGenerationOperation(RunOptions runOptions, BdioOptions bdioOptions, BdioManager bdioManager, EventSystem eventSystem) {
        this.runOptions = runOptions;
        this.bdioOptions = bdioOptions;
        this.bdioManager = bdioManager;
        this.eventSystem = eventSystem;
    }

    @Override
    public boolean shouldExecute() {
        return true;
    }

    @Override
    public String getOperationName() {
        return "Bdio Generation";
    }

    @Override
    public OperationResult<BdioResult> executeOperation(BdioInput input) throws DetectUserFriendlyException, IntegrationException {
        BdioResult bdioResult = bdioManager.createBdioFiles(bdioOptions, input.getAggregateOptions(), input.getNameVersion(), input.getCodeLocations(), runOptions.shouldUseBdio2());
        eventSystem.publishEvent(Event.DetectCodeLocationNamesCalculated, bdioResult.getCodeLocationNamesResult());
        return OperationResult.success(bdioResult);
    }
}
