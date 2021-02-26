/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector;

import java.io.File;
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
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.util.NameVersion;

public class DetectorToolResult {
    @Nullable
    private final NameVersion bomToolProjectNameVersion;
    private final List<DetectCodeLocation> bomToolCodeLocations;

    private final Set<DetectorType> applicableDetectorTypes;
    private final Set<DetectorType> failedDetectorTypes;

    @Nullable
    private final DetectorEvaluationTree rootDetectorEvaluationTree;
    private final Map<CodeLocation, DetectCodeLocation> codeLocationMap;

    public DetectorToolResult(@Nullable final NameVersion bomToolProjectNameVersion, final List<DetectCodeLocation> bomToolCodeLocations, final Set<DetectorType> applicableDetectorTypes,
        final Set<DetectorType> failedDetectorTypes, @Nullable final DetectorEvaluationTree rootDetectorEvaluationTree,
        final Map<CodeLocation, DetectCodeLocation> codeLocationMap) {
        this.bomToolProjectNameVersion = bomToolProjectNameVersion;
        this.bomToolCodeLocations = bomToolCodeLocations;
        this.applicableDetectorTypes = applicableDetectorTypes;
        this.failedDetectorTypes = failedDetectorTypes;
        this.rootDetectorEvaluationTree = rootDetectorEvaluationTree;
        this.codeLocationMap = codeLocationMap;
    }

    public DetectorToolResult() {
        this.bomToolProjectNameVersion = new NameVersion();
        this.bomToolCodeLocations = new ArrayList<>();
        this.applicableDetectorTypes = new HashSet<>();
        this.failedDetectorTypes = new HashSet<>();
        this.rootDetectorEvaluationTree = new DetectorEvaluationTree(new File(""), 0, null, new ArrayList<>(), new HashSet<>());
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

    public Optional<DetectorEvaluationTree> getRootDetectorEvaluationTree() {
        return Optional.ofNullable(rootDetectorEvaluationTree);
    }

    public Map<CodeLocation, DetectCodeLocation> getCodeLocationMap() {
        return codeLocationMap;
    }

}
