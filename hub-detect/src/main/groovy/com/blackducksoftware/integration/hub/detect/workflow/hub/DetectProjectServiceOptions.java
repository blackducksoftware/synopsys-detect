package com.blackducksoftware.integration.hub.detect.workflow.hub;

public class DetectProjectServiceOptions {
    private final String projectVersionPhase;
    private final String projectVersionDistribution;
    private final int projectTier;
    private final String projectDescription;
    private final String projectVersionNotes;
    private final String[] cloneCategories;
    private final boolean projectLevelAdjustments;
    private final boolean forceProjectVersionUpdate;
    private final String cloneVersionName;

    public DetectProjectServiceOptions(final String projectVersionPhase, final String projectVersionDistribution, final int projectTier, final String projectDescription, final String projectVersionNotes,
        final String[] cloneCategories, final boolean projectLevelAdjustments, final boolean forceProjectVersionUpdate, final String cloneVersionName) {
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

    public int getProjectTier() {
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

    public boolean isProjectLevelAdjustments() {
        return projectLevelAdjustments;
    }

    public boolean isForceProjectVersionUpdate() {
        return forceProjectVersionUpdate;
    }

    public String getCloneVersionName() {
        return cloneVersionName;
    }
}
