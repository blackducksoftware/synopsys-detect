package com.blackducksoftware.integration.hub.detect.workflow.hub;

public class DetectProjectServiceOptions {
    private final String projectVersionPhase;
    private final String projectVersionDistribution;
    private final Integer projectTier;
    private final String projectDescription;
    private final String projectVersionNotes;
    private final String[] cloneCategories;
    private final Boolean projectLevelAdjustments;
    private final Boolean forceProjectVersionUpdate;
    private final String cloneVersionName;

    public DetectProjectServiceOptions(final String projectVersionPhase, final String projectVersionDistribution, final Integer projectTier, final String projectDescription, final String projectVersionNotes,
        final String[] cloneCategories, final Boolean projectLevelAdjustments, final Boolean forceProjectVersionUpdate, final String cloneVersionName) {
        this.projectVersionPhase = projectVersionPhase;
        this.projectVersionDistribution = projectVersionDistribution;
        this.projectTier = projectTier;
        this.projectDescription = projectDescription;
        this.projectVersionNotes = projectVersionNotes;
        this.cloneCategories = cloneCategories;
        this.projectLevelAdjustments = projectLevelAdjustments;
        this.forceProjectVersionUpdate = forceProjectVersionUpdate;
        this.cloneVersionName = cloneVersionName;
    }

    public String getProjectVersionPhase() {
        return projectVersionPhase;
    }

    public String getProjectVersionDistribution() {
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

    public String[] getCloneCategories() {
        return cloneCategories;
    }

    public Boolean isProjectLevelAdjustments() {
        return projectLevelAdjustments;
    }

    public Boolean isForceProjectVersionUpdate() {
        return forceProjectVersionUpdate;
    }

    public String getCloneVersionName() {
        return cloneVersionName;
    }
}
