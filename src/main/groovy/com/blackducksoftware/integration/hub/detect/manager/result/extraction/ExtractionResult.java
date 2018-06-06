package com.blackducksoftware.integration.hub.detect.manager.result.extraction;

import java.util.HashSet;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

public class ExtractionResult {
    HashSet<BomToolType> failedBomTools;
    HashSet<BomToolType> successfulBomTools;

    public ExtractionResult(final List<DetectCodeLocation> detectCodeLocations, final String recommendedProjectName, final String recommendedProjectVersion, final HashSet<BomToolType> successfulBomTools, final HashSet<BomToolType> failedBomTools) {
        this.failedBomTools = failedBomTools;
        this.successfulBomTools = successfulBomTools;
        this.recommendedProjectName = recommendedProjectName;
        this.recommendedProjectVersion = recommendedProjectVersion;
        this.detectCodeLocations = detectCodeLocations;
    }

    private final String recommendedProjectName;
    private final String recommendedProjectVersion;
    private final List<DetectCodeLocation> detectCodeLocations;

    public boolean getSuccess() {
        return true;
    }

    public List<DetectCodeLocation> getDetectCodeLocations() {
        return detectCodeLocations;
    }

    public String getRecommendedProjectName() {
        return recommendedProjectName;
    }

    public String getRecommendedProjectVersion() {
        return recommendedProjectVersion;
    }

    public HashSet<BomToolType> getSuccessfulBomToolTypes() {
        return successfulBomTools;
    }

    public HashSet<BomToolType> getFailedBomToolTypes() {
        return failedBomTools;
    }
}
