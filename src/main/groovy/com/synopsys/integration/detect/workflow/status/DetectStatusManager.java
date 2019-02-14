/**
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.status;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.log.IntLogger;

public class DetectStatusManager {
    private List<Status> statusSummaries = new ArrayList<>();

    public DetectStatusManager(EventSystem eventSystem) {
        eventSystem.registerListener(Event.StatusSummary, event -> addStatusSummary(event));
    }

    public void addStatusSummary(Status status) {
        statusSummaries.add(status);
    }

    public void logDetectResults(final IntLogger logger, final ExitCodeType exitCodeType) {
        new DetectStatusLogger().logDetectResults(logger, statusSummaries, exitCodeType);
    }

    public boolean hasAnyFailure() {
        return statusSummaries.stream()
                   .anyMatch(it -> it.getStatusType() == StatusType.FAILURE);
    }
}
