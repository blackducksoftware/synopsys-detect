package com.blackducksoftware.integration.hub.detect.tool.detector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorEvaluation;
import com.synopsys.integration.util.NameVersion;

public class DetectorToolResult {
    public Optional<NameVersion> bomToolProjectNameVersion;
    public List<DetectCodeLocation> bomToolCodeLocations;

    public Set<DetectorType> applicableDetectorTypes = new HashSet<>();
    public Set<DetectorType> failedDetectorTypes = new HashSet<>();
    public Set<DetectorType> succesfullDetectorTypes = new HashSet<>();

    public List<DetectorEvaluation> evaluatedBomTools = new ArrayList<>();

}
