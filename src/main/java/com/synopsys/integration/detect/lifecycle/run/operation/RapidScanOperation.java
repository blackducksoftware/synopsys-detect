/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation;

import java.util.List;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.manual.view.DeveloperScanComponentResultView;
import com.synopsys.integration.blackduck.developermode.RapidScanService;
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
        RapidScanService developerScanService = blackDuckServicesFactory.createRapidScanService();
        BlackDuckRapidMode rapidScanMode = new BlackDuckRapidMode(blackDuckRunData, developerScanService, timeoutInSeconds);
        BlackDuckRapidModePostActions postActions = new BlackDuckRapidModePostActions(gson, eventSystem, directoryManager);

        List<DeveloperScanComponentResultView> results = rapidScanMode.run(input.getBdioResult());
        postActions.perform(input.getProjectNameVersion(), results);
    }
}
