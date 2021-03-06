/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.project;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectCloneCategoriesType;
import com.synopsys.integration.blackduck.service.dataservice.ProjectService;
import com.synopsys.integration.blackduck.service.model.ProjectSyncModel;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.blackduck.project.options.CloneFindResult;
import com.synopsys.integration.detect.workflow.blackduck.project.options.ProjectSyncOptions;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class SyncProjectOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProjectService projectService;

    public SyncProjectOperation(ProjectService projectService) {
        this.projectService = projectService;
    }

    public ProjectVersionWrapper sync(NameVersion projectNameVersion, CloneFindResult cloneFindResult, ProjectSyncOptions projectSyncOptions) throws DetectUserFriendlyException, IntegrationException {
        ProjectSyncModel projectSyncModel = createProjectSyncModel(projectNameVersion, cloneFindResult, projectSyncOptions);
        boolean forceUpdate = projectSyncOptions.isForceProjectVersionUpdate();
        return projectService.syncProjectAndVersion(projectSyncModel, forceUpdate);
    }

    public ProjectSyncModel createProjectSyncModel(NameVersion projectNameVersion, CloneFindResult cloneFindResult, ProjectSyncOptions projectSyncOptions) throws DetectUserFriendlyException {
        ProjectSyncModel projectSyncModel = ProjectSyncModel.createWithDefaults(projectNameVersion.getName(), projectNameVersion.getVersion());

        // TODO: Handle a boolean property not being set in detect configuration - ie need to determine if this property actually exists in the ConfigurableEnvironment - just omit this one?
        projectSyncModel.setProjectLevelAdjustments(projectSyncOptions.getProjectLevelAdjustments());

        Optional.ofNullable(projectSyncOptions.getProjectVersionPhase()).ifPresent(projectSyncModel::setPhase);
        Optional.ofNullable(projectSyncOptions.getProjectVersionDistribution()).ifPresent(projectSyncModel::setDistribution);

        Integer projectTier = projectSyncOptions.getProjectTier();
        if (null != projectTier && projectTier >= 1 && projectTier <= 5) {
            projectSyncModel.setProjectTier(projectTier);
        }

        String description = projectSyncOptions.getProjectDescription();
        if (StringUtils.isNotBlank(description)) {
            projectSyncModel.setDescription(description);
        }

        String releaseComments = projectSyncOptions.getProjectVersionNotes();
        if (StringUtils.isNotBlank(releaseComments)) {
            projectSyncModel.setReleaseComments(releaseComments);
        }

        List<ProjectCloneCategoriesType> cloneCategories = projectSyncOptions.getCloneCategories();
        if (!cloneCategories.isEmpty()) {
            projectSyncModel.setCloneCategories(cloneCategories);
        }

        String nickname = projectSyncOptions.getProjectVersionNickname();
        if (StringUtils.isNotBlank(nickname)) {
            projectSyncModel.setNickname(nickname);
        }

        if (cloneFindResult.getCloneUrl().isPresent()) {
            logger.debug("Cloning project version from release url: " + cloneFindResult.getCloneUrl().get());
            projectSyncModel.setCloneFromReleaseUrl(cloneFindResult.getCloneUrl().get().string());
        }

        return projectSyncModel;
    }
}
