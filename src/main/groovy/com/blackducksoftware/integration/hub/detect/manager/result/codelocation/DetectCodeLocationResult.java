package com.blackducksoftware.integration.hub.detect.manager.result.codelocation;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.hub.detect.model.BdioCodeLocation;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

public class DetectCodeLocationResult {
    List<BdioCodeLocation> bdioCodeLocations;
    Map<DetectCodeLocation, String> codeLocationNames;
    Set<BomToolType> failedBomTools;

    public DetectCodeLocationResult(final List<BdioCodeLocation> bdioCodeLocations, final Set<BomToolType> failedBomTools, final Map<DetectCodeLocation, String> codeLocationNames) {
        this.bdioCodeLocations = bdioCodeLocations;
        this.failedBomTools = failedBomTools;
        this.codeLocationNames = codeLocationNames;
    }

    public Map<DetectCodeLocation, String> getCodeLocationNames() {
        return codeLocationNames;
    }

    public List<BdioCodeLocation> getBdioCodeLocations() {
        return bdioCodeLocations;
    }

    public Set<BomToolType> getFailedBomToolTypes() {
        return failedBomTools;
    }
}
