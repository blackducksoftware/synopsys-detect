/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.blackduck;

import java.util.Optional;

import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.run.operation.input.FullScanPostProcessingInput;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckPostActions;
import com.synopsys.integration.detect.workflow.blackduck.BlackDuckPostOptions;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.result.BlackDuckBomDetectResult;
import com.synopsys.integration.detect.workflow.result.DetectResult;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpUrl;

public class FullScanPostProcessingOperation {
    private final DetectToolFilter detectToolFilter;
    private final BlackDuckPostOptions blackDuckPostOptions;
    private final EventSystem eventSystem;
    private final Long detectTimeoutInSeconds;

    public FullScanPostProcessingOperation(DetectToolFilter detectToolFilter, BlackDuckPostOptions blackDuckPostOptions,
        EventSystem eventSystem, Long detectTimeoutInSeconds) {
        this.detectToolFilter = detectToolFilter;
        this.blackDuckPostOptions = blackDuckPostOptions;
        this.eventSystem = eventSystem;
        this.detectTimeoutInSeconds = detectTimeoutInSeconds;
    }

    public void execute(BlackDuckServicesFactory blackDuckServicesFactory, FullScanPostProcessingInput postProcessingInput) throws DetectUserFriendlyException, IntegrationException {
        BlackDuckPostActions blackDuckPostActions = new BlackDuckPostActions(blackDuckServicesFactory.createCodeLocationCreationService(), eventSystem, blackDuckServicesFactory.getBlackDuckApiClient(),
            blackDuckServicesFactory.createProjectBomService(), blackDuckServicesFactory.createReportService(detectTimeoutInSeconds * 1000));
        blackDuckPostActions
            .perform(blackDuckPostOptions, postProcessingInput.getCodeLocationResults().getCodeLocationWaitData(), postProcessingInput.getProjectVersionWrapper(), postProcessingInput.getProjectNameVersion(), detectTimeoutInSeconds);

        if ((!postProcessingInput.getBdioResult().getUploadTargets().isEmpty() || detectToolFilter.shouldInclude(DetectTool.SIGNATURE_SCAN))) {
            Optional<String> componentsLink = Optional.ofNullable(postProcessingInput.getProjectVersionWrapper())
                                                  .map(ProjectVersionWrapper::getProjectVersionView)
                                                  .flatMap(projectVersionView -> projectVersionView.getFirstLinkSafely(ProjectVersionView.COMPONENTS_LINK))
                                                  .map(HttpUrl::string);

            if (componentsLink.isPresent()) {
                DetectResult detectResult = new BlackDuckBomDetectResult(componentsLink.get());
                eventSystem.publishEvent(Event.ResultProduced, detectResult);
            }
        }
    }
}
