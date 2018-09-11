package com.blackducksoftware.integration.hub.detect.workflow.project;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.BdioCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;

public class BdioResult {
    private final List<BdioCodeLocation> bdioCodeLocations;
    private final Map<DetectCodeLocation, String> codeLocationNames;
    private final Set<BomToolGroupType> failedBomToolGroups;

    public BdioResult(final List<BdioCodeLocation> bdioCodeLocations, final Set<BomToolGroupType> failedBomToolGroups, final Map<DetectCodeLocation, String> codeLocationNames) {
        this.bdioCodeLocations = bdioCodeLocations;
        this.failedBomToolGroups = failedBomToolGroups;
        this.codeLocationNames = codeLocationNames;
    }

    public Map<DetectCodeLocation, String> getCodeLocationNames() {
        return codeLocationNames;
    }

    public List<BdioCodeLocation> getBdioCodeLocations() {
        return bdioCodeLocations;
    }

    public Set<BomToolGroupType> getFailedBomToolGroupTypes() {
        return failedBomToolGroups;
    }
}
