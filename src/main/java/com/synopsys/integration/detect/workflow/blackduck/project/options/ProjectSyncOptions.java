/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.project.options;

import java.util.List;

import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectCloneCategoriesType;
import com.synopsys.integration.blackduck.api.generated.enumeration.ProjectVersionDistributionType;
import com.synopsys.integration.blackduck.api.manual.temporary.enumeration.ProjectVersionPhaseType;

public class ProjectSyncOptions {
    private final ProjectVersionPhaseType projectVersionPhase;
    private final ProjectVersionDistributionType projectVersionDistribution;
    private final Integer projectTier;
    private final String projectDescription;
    private final String projectVersionNotes;
    private final List<ProjectCloneCategoriesType> cloneCategories;
    private final Boolean forceProjectVersionUpdate;
    private final String projectVersionNickname;
    private final Boolean projectLevelAdjustments;

    public ProjectSyncOptions(final ProjectVersionPhaseType projectVersionPhase, final ProjectVersionDistributionType projectVersionDistribution, final Integer projectTier,
        final String projectDescription, final String projectVersionNotes,
        final List<ProjectCloneCategoriesType> cloneCategories, final Boolean forceProjectVersionUpdate, final String projectVersionNickname, Boolean projectLevelAdjustments) {
        this.projectVersionPhase = projectVersionPhase;
        this.projectVersionDistribution = projectVersionDistribution;
        this.projectTier = projectTier;
        this.projectDescription = projectDescription;
        this.projectVersionNotes = projectVersionNotes;
        this.cloneCategories = cloneCategories;
        this.forceProjectVersionUpdate = forceProjectVersionUpdate;
        this.projectVersionNickname = projectVersionNickname;
        this.projectLevelAdjustments = projectLevelAdjustments;
    }

    public ProjectVersionPhaseType getProjectVersionPhase() {
        return projectVersionPhase;
    }

    public ProjectVersionDistributionType getProjectVersionDistribution() {
        return projectVersionDistribution;
    }

    public Integer getProjectTier() {
        return projectTier;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public String getProjectVersionNotes() {
        return projectVersionNotes;
    }

    public List<ProjectCloneCategoriesType> getCloneCategories() {
        return cloneCategories;
    }

    public Boolean isForceProjectVersionUpdate() {
        return forceProjectVersionUpdate;
    }

    public String getProjectVersionNickname() {
        return projectVersionNickname;
    }

    public Boolean getProjectLevelAdjustments() {
        return projectLevelAdjustments;
    }
}
