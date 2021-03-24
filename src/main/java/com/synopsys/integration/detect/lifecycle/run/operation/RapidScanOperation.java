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
import com.synopsys.integration.blackduck.scan.RapidScanService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.input.RapidScanInput;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.workflow.blackduck.developer.BlackDuckRapidMode;
import com.synopsys.integration.detect.workflow.blackduck.developer.BlackDuckRapidModePostActions;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.exception.IntegrationException;

public class RapidScanOperation {
    private final Gson gson;
    private final StatusEventPublisher statusEventPublisher;
    private final ExitCodePublisher exitCodePublisher;
    private final DirectoryManager directoryManager;
    private final OperationSystem operationSystem;
    private final Long timeoutInSeconds;

    public RapidScanOperation(Gson gson, StatusEventPublisher statusEventPublisher, ExitCodePublisher exitCodePublisher, DirectoryManager directoryManager, OperationSystem operationSystem, Long timeoutInSeconds) {
        this.gson = gson;
        this.statusEventPublisher = statusEventPublisher;
        this.exitCodePublisher = exitCodePublisher;
        this.directoryManager = directoryManager;
        this.operationSystem = operationSystem;
        this.timeoutInSeconds = timeoutInSeconds;
    }

    public void execute(BlackDuckRunData blackDuckRunData, BlackDuckServicesFactory blackDuckServicesFactory, RapidScanInput input) throws DetectUserFriendlyException, IntegrationException {
        RapidScanService developerScanService = blackDuckServicesFactory.createRapidScanService();
        BlackDuckRapidMode rapidScanMode = new BlackDuckRapidMode(statusEventPublisher, blackDuckRunData, developerScanService, timeoutInSeconds, operationSystem);
        BlackDuckRapidModePostActions postActions = new BlackDuckRapidModePostActions(gson, statusEventPublisher, exitCodePublisher, directoryManager, operationSystem);

        List<DeveloperScanComponentResultView> results = rapidScanMode.run(input.getBdioResult());
        postActions.perform(input.getProjectNameVersion(), results);
    }
}
