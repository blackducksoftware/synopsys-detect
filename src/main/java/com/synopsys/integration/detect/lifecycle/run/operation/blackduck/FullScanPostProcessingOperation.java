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

import java.util.Optional;

import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationResult;
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

public class FullScanPostProcessingOperation extends BlackDuckOnlineOperation<FullScanPostProcessingInput, Void> {
    private final DetectToolFilter detectToolFilter;
    private final BlackDuckPostOptions blackDuckPostOptions;
    private final EventSystem eventSystem;
    private final Long detectTimeoutInSeconds;

    public FullScanPostProcessingOperation(ProductRunData productRunData, DetectToolFilter detectToolFilter, BlackDuckPostOptions blackDuckPostOptions,
        EventSystem eventSystem, Long detectTimeoutInSeconds) {
        super(productRunData);
        this.detectToolFilter = detectToolFilter;
        this.blackDuckPostOptions = blackDuckPostOptions;
        this.eventSystem = eventSystem;
        this.detectTimeoutInSeconds = detectTimeoutInSeconds;
    }

    @Override
    public String getOperationName() {
        return "Black Duck Post Full Scan";
    }

    @Override
    protected OperationResult<Void> executeOperation(FullScanPostProcessingInput input) throws DetectUserFriendlyException, IntegrationException {
        BlackDuckServicesFactory blackDuckServicesFactory = getBlackDuckServicesFactory();
        BlackDuckPostActions blackDuckPostActions = new BlackDuckPostActions(blackDuckServicesFactory.createCodeLocationCreationService(), eventSystem, blackDuckServicesFactory.getBlackDuckApiClient(),
            blackDuckServicesFactory.createProjectBomService(), blackDuckServicesFactory.createReportService(detectTimeoutInSeconds));
        blackDuckPostActions.perform(blackDuckPostOptions, input.getCodeLocationResults().getCodeLocationWaitData(), input.getProjectVersionWrapper(), input.getProjectNameVersion(), detectTimeoutInSeconds);

        if ((!input.getBdioResult().getUploadTargets().isEmpty() || detectToolFilter.shouldInclude(DetectTool.SIGNATURE_SCAN))) {
            Optional<String> componentsLink = Optional.ofNullable(input.getProjectVersionWrapper())
                                                  .map(ProjectVersionWrapper::getProjectVersionView)
                                                  .flatMap(projectVersionView -> projectVersionView.getFirstLinkSafely(ProjectVersionView.COMPONENTS_LINK))
                                                  .map(HttpUrl::string);

            if (componentsLink.isPresent()) {
                DetectResult detectResult = new BlackDuckBomDetectResult(componentsLink.get());
                eventSystem.publishEvent(Event.ResultProduced, detectResult);
            }
        }

        return OperationResult.success();
    }
}
