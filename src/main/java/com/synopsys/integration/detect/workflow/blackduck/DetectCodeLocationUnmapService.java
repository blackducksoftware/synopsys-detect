/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.view.CodeLocationView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.dataservice.CodeLocationService;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.exception.IntegrationException;

public class DetectCodeLocationUnmapService {
    private static final String OPERATION_NAME = "Black Duck Unmap Code Locations";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BlackDuckApiClient blackDuckService;
    private final CodeLocationService codeLocationService;
    private final OperationSystem operationSystem;

    public DetectCodeLocationUnmapService(BlackDuckApiClient blackDuckService, CodeLocationService codeLocationService, OperationSystem operationSystem) {
        this.blackDuckService = blackDuckService;
        this.codeLocationService = codeLocationService;
        this.operationSystem = operationSystem;
    }

    public void unmapCodeLocations(ProjectVersionView projectVersionView) throws DetectUserFriendlyException {
        operationSystem.beginOperation(OPERATION_NAME);
        try {
            List<CodeLocationView> codeLocationViews = blackDuckService.getAllResponses(projectVersionView, ProjectVersionView.CODELOCATIONS_LINK_RESPONSE);

            for (CodeLocationView codeLocationView : codeLocationViews) {
                codeLocationService.unmapCodeLocation(codeLocationView);
            }
            logger.info("Successfully unmapped (" + codeLocationViews.size() + ") code locations.");
            operationSystem.completeWithSuccess(OPERATION_NAME);
        } catch (IntegrationException e) {
            String errorMessage = String.format("There was a problem unmapping Code Locations: %s", e.getMessage());
            operationSystem.completeWithError(OPERATION_NAME, errorMessage);
            throw new DetectUserFriendlyException(errorMessage, e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }

    }
}
