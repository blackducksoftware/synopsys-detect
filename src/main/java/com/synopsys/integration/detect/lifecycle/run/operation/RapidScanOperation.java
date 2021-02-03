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

import java.util.List;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.blackduck.developermode.DeveloperScanService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.input.RapidScanInput;
import com.synopsys.integration.detect.workflow.blackduck.developer.BlackDuckRapidMode;
import com.synopsys.integration.detect.workflow.blackduck.developer.BlackDuckRapidModePostActions;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.exception.IntegrationException;

public class RapidScanOperation {
    private final Gson gson;
    private final EventSystem eventSystem;
    private final DirectoryManager directoryManager;
    private final Long timeoutInSeconds;

    public RapidScanOperation(Gson gson, EventSystem eventSystem, DirectoryManager directoryManager, Long timeoutInSeconds) {
        this.gson = gson;
        this.eventSystem = eventSystem;
        this.directoryManager = directoryManager;
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public void execute(BlackDuckRunData blackDuckRunData, BlackDuckServicesFactory blackDuckServicesFactory, RapidScanInput input) throws DetectUserFriendlyException, IntegrationException {
        DeveloperScanService developerScanService = blackDuckServicesFactory.createDeveloperScanService();
        BlackDuckRapidMode rapidScanMode = new BlackDuckRapidMode(blackDuckRunData, developerScanService, timeoutInSeconds);
        BlackDuckRapidModePostActions postActions = new BlackDuckRapidModePostActions(gson, eventSystem, directoryManager);

        List<DeveloperScanComponentResultView> results = rapidScanMode.run(input.getBdioResult());
        postActions.perform(input.getProjectNameVersion(), results);
    }
}
