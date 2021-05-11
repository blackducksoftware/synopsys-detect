/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.blackduck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.blackduck.DetectCodeLocationUnmapService;
import com.synopsys.integration.detect.workflow.blackduck.DetectCustomFieldService;
import com.synopsys.integration.detect.workflow.blackduck.DetectProjectService;
import com.synopsys.integration.detect.workflow.blackduck.DetectProjectServiceOptions;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class ProjectCreationOperation {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DetectProjectServiceOptions detectProjectServiceOptions;
    private final DetectCustomFieldService detectCustomFieldService;
    private final OperationSystem operationSystem;
    private final boolean shouldUnmap;

    public ProjectCreationOperation(Boolean shouldUnmap, DetectProjectServiceOptions detectProjectServiceOptions,
        DetectCustomFieldService detectCustomFieldService, OperationSystem operationSystem) {
        this.shouldUnmap = shouldUnmap;
        this.detectProjectServiceOptions = detectProjectServiceOptions;
        this.detectCustomFieldService = detectCustomFieldService;
        this.operationSystem = operationSystem;
    }

    public ProjectVersionWrapper execute(BlackDuckServicesFactory blackDuckServicesFactory, NameVersion projectNameVersion) throws DetectUserFriendlyException, IntegrationException {
        DetectProjectService detectProjectService = new DetectProjectService(blackDuckServicesFactory.getBlackDuckApiClient(), blackDuckServicesFactory.createProjectService(),
            blackDuckServicesFactory.createProjectBomService(), blackDuckServicesFactory.createProjectUsersService(), blackDuckServicesFactory.createTagService(), detectProjectServiceOptions,
            blackDuckServicesFactory.createProjectMappingService(), detectCustomFieldService, operationSystem);
        DetectCodeLocationUnmapService detectCodeLocationUnmapService = new DetectCodeLocationUnmapService(blackDuckServicesFactory.getBlackDuckApiClient(), blackDuckServicesFactory.createCodeLocationService());

        ProjectVersionWrapper projectVersionWrapper = detectProjectService.createOrUpdateBlackDuckProject(projectNameVersion);
        if (null != projectVersionWrapper && shouldUnmap) {
            logger.debug("Unmapping code locations.");
            detectCodeLocationUnmapService.unmapCodeLocations(projectVersionWrapper.getProjectVersionView());
        } else {
            logger.debug("Will not unmap code locations: Project view was not present, or should not unmap code locations.");
        }
        return projectVersionWrapper;
    }
}
