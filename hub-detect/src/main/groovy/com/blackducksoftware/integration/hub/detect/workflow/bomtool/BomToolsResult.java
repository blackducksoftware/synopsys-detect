package com.blackducksoftware.integration.hub.detect.workflow.bomtool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;

public class BomToolsResult {
    public List<DetectCodeLocation> bomToolCodeLocations;

    public Set<BomToolGroupType> failedBomToolGroupTypes = new HashSet<>();
    public Set<BomToolGroupType> succesfullBomToolGroupTypes = new HashSet<>();

    public List<BomToolEvaluation> evaluatedBomTools = new ArrayList<>();

}
