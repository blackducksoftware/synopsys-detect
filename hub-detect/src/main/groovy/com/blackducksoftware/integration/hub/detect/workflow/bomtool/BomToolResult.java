package com.blackducksoftware.integration.hub.detect.workflow.bomtool;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.util.NameVersion;

public class BomToolResult {
    public NameVersion bomToolProjectInfo;
    public List<DetectCodeLocation> bomToolCodeLocations;

    public ExitCodeType exitCodeType;
    public Set<BomToolGroupType> failedBomToolGroupTypes = new HashSet<>();
    public Set<BomToolGroupType> succesfullBomToolGroupTypes = new HashSet<>();

    public Set<BomToolGroupType> missingBomToolGroupTypes = new HashSet<>();

}
