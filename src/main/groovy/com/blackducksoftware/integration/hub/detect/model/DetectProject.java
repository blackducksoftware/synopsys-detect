package com.blackducksoftware.integration.hub.detect.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.service.model.ProjectRequestBuilder;

public class DetectProject {
    private String projectName;
    private String projectVersionName;
    private final List<DetectCodeLocation> detectCodeLocations = new ArrayList<>();

    public String getProjectName() {
        return projectName;
    }

    public String getProjectVersionName() {
        return projectVersionName;
    }

    /**
     * Only the DetectProjectManager should invoke this method.
     */
    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    /**
     * Only the DetectProjectManager should invoke this method.
     */
    public void setProjectVersionName(final String projectVersionName) {
        this.projectVersionName = projectVersionName;
    }

    public void setProjectNameIfNotSet(final String projectName) {
        if (StringUtils.isBlank(this.projectName)) {
            this.projectName = projectName;
        }
    }

    public void setProjectVersionNameIfNotSet(final String projectVersionName) {
        if (StringUtils.isBlank(this.projectVersionName)) {
            this.projectVersionName = projectVersionName;
        }
    }

    public void addAllDetectCodeLocations(final List<DetectCodeLocation> detectCodeLocations) {
        detectCodeLocations
                .stream()
                .forEach(it -> addDetectCodeLocation(it));
    }

    public void addDetectCodeLocation(final DetectCodeLocation detectCodeLocation) {
        setProjectNameIfNotSet(detectCodeLocation.getBomToolProjectName());
        setProjectVersionNameIfNotSet(detectCodeLocation.getBomToolProjectVersionName());

        detectCodeLocations.add(detectCodeLocation);
    }

    public List<DetectCodeLocation> getDetectCodeLocations() {
        return detectCodeLocations;
    }

    public ProjectRequestBuilder createDefaultProjectRequestBuilder(final DetectConfiguration detectConfiguration) {
        final ProjectRequestBuilder builder = new ProjectRequestBuilder();
        builder.setProjectName(getProjectName());
        builder.setVersionName(getProjectVersionName());
        builder.setProjectLevelAdjustments(detectConfiguration.getProjectLevelMatchAdjustments());
        builder.setPhase(detectConfiguration.getProjectVersionPhase());
        builder.setDistribution(detectConfiguration.getProjectVersionDistribution());
        builder.setProjectTier(detectConfiguration.getProjectTier());
        builder.setReleaseComments(detectConfiguration.getProjectVersionNotes());

        return builder;
    }

}
