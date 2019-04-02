/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.workflow.hub;

import java.util.List;

import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.blackduck.api.generated.view.CodeLocationView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.blackduck.service.CodeLocationService;
import com.synopsys.integration.exception.IntegrationException;

public class DetectCodeLocationUnmapService {
    private final BlackDuckService hubService;
    private CodeLocationService codeLocationService;

    public DetectCodeLocationUnmapService(BlackDuckService hubService, CodeLocationService codeLocationService) {
        this.hubService = hubService;
        this.codeLocationService = codeLocationService;
    }

    public void unmapCodeLocations(ProjectVersionView projectVersionView) throws DetectUserFriendlyException {
        try {
            final List<CodeLocationView> codeLocationViews = hubService.getAllResponses(projectVersionView, ProjectVersionView.CODELOCATIONS_LINK_RESPONSE);

            for (final CodeLocationView codeLocationView : codeLocationViews) {
                codeLocationService.unmapCodeLocation(codeLocationView);
            }
        } catch (final IntegrationException e) {
            throw new DetectUserFriendlyException(String.format("There was a problem unmapping Code Locations: %s", e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }

    }
}
