package com.blackducksoftware.integration.hub.detect.workflow.bomtool;

import java.util.List;

import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.util.NameVersion;

public class BomToolResult {
    public NameVersion bomToolProjectInfo;
    public List<DetectCodeLocation> bomToolCodeLocations;
}
