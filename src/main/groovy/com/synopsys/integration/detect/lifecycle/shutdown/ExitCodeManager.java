/**
 * detect-application
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.synopsys.integration.detect.lifecycle.shutdown;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;

public class ExitCodeManager {
    private List<ExitCodeRequest> exitCodeRequests = new ArrayList<>();
    private ExitCodeUtility exitCodeUtility;

    public ExitCodeManager(EventSystem eventSystem, ExitCodeUtility exitCodeUtility) {
        this.exitCodeUtility = exitCodeUtility;
        eventSystem.registerListener(Event.ExitCode, event -> addExitCodeRequest(event));
    }

    public void requestExitCode(Exception e) {
        requestExitCode(exitCodeUtility.getExitCodeFromExceptionDetails(e));
    }

    public void requestExitCode(ExitCodeType exitCodeType) {
        exitCodeRequests.add(new ExitCodeRequest(exitCodeType));
    }

    public void addExitCodeRequest(ExitCodeRequest request) {
        exitCodeRequests.add(request);
    }

    public ExitCodeType getWinningExitCode() {
        ExitCodeType winningExitCodeType = ExitCodeType.SUCCESS;
        for (ExitCodeRequest exitCodeRequest : exitCodeRequests) {
            winningExitCodeType = ExitCodeType.getWinningExitCodeType(winningExitCodeType, exitCodeRequest.getExitCodeType());
        }
        return winningExitCodeType;
    }
}
