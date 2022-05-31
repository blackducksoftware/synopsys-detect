package com.synopsys.integration.detect.tool.detector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detector.accuracy.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.util.NameVersion;

public class DetectorToolResult {
    @Nullable
    private final NameVersion bomToolProjectNameVersion;
    private final List<DetectCodeLocation> bomToolCodeLocations;

    private final Set<DetectorType> applicableDetectorTypes;
    private final Set<DetectorType> failedDetectorTypes;

    @Nullable
    private final DetectorEvaluation rootDetectorEvaluation;
    private final Map<CodeLocation, DetectCodeLocation> codeLocationMap;

    public DetectorToolResult(
        @Nullable NameVersion bomToolProjectNameVersion,
        List<DetectCodeLocation> bomToolCodeLocations,
        Set<DetectorType> applicableDetectorTypes,
        Set<DetectorType> failedDetectorTypes,
        @Nullable DetectorEvaluation rootDetectorEvaluation,
        Map<CodeLocation, DetectCodeLocation> codeLocationMap
    ) {
        this.bomToolProjectNameVersion = bomToolProjectNameVersion;
        this.bomToolCodeLocations = bomToolCodeLocations;
        this.applicableDetectorTypes = applicableDetectorTypes;
        this.failedDetectorTypes = failedDetectorTypes;
        this.rootDetectorEvaluation = rootDetectorEvaluation;
        this.codeLocationMap = codeLocationMap;
    }

    public DetectorToolResult() {
        this.bomToolProjectNameVersion = new NameVersion();
        this.bomToolCodeLocations = new ArrayList<>();
        this.applicableDetectorTypes = new HashSet<>();
        this.failedDetectorTypes = new HashSet<>();
        this.rootDetectorEvaluation = null;
        this.codeLocationMap = new HashMap<>();
    }

    public Optional<NameVersion> getBomToolProjectNameVersion() {
        return Optional.ofNullable(bomToolProjectNameVersion);
    }

    public List<DetectCodeLocation> getBomToolCodeLocations() {
        return bomToolCodeLocations;
    }

    public Set<DetectorType> getApplicableDetectorTypes() {
        return applicableDetectorTypes;
    }

    public Set<DetectorType> getFailedDetectorTypes() {
        return failedDetectorTypes;
    }

    public boolean anyDetectorsFailed() {
        return !getFailedDetectorTypes().isEmpty();
    }

    public Optional<DetectorEvaluation> getRootDetectorEvaluation() {
        return Optional.ofNullable(rootDetectorEvaluation);
    }

    public Map<CodeLocation, DetectCodeLocation> getCodeLocationMap() {
        return codeLocationMap;
    }

}
