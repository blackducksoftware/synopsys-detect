/*
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
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationAccumulator;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationResultCalculator;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationResults;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.exception.IntegrationException;

public class CodeLocationResultCalculationOperation {
    private final CodeLocationResultCalculator codeLocationResultCalculator;
    private final EventSystem eventSystem;

    public CodeLocationResultCalculationOperation(CodeLocationResultCalculator codeLocationResultCalculator, EventSystem eventSystem) {
        this.codeLocationResultCalculator = codeLocationResultCalculator;
        this.eventSystem = eventSystem;
    }

    public CodeLocationResults execute(CodeLocationAccumulator codeLocationAccumulator) throws DetectUserFriendlyException, IntegrationException {
        CodeLocationResults codeLocationResults = codeLocationResultCalculator.calculateCodeLocationResults(codeLocationAccumulator);
        eventSystem.publishEvent(Event.CodeLocationsCompleted, codeLocationResults.getAllCodeLocationNames());
        return codeLocationResults;
    }
}
