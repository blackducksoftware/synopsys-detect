/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.tool.detector.DetectorToolResult;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.project.DetectToolProjectInfo;
import com.synopsys.integration.util.NameVersion;

public class UniversalToolsResultBuilder {
    private DockerTargetData dockerTargetData = null;
    private final List<DetectToolProjectInfo> detectToolProjectInfo = new ArrayList<>();
    private final List<DetectCodeLocation> detectCodeLocations = new ArrayList<>();
    private boolean anyFailed = false;

    public void addDetectableToolResult(DetectableToolResult detectableToolResult) {
        detectableToolResult.getDetectToolProjectInfo().ifPresent(detectToolProjectInfo1 -> addToolNameVersion(detectToolProjectInfo1.getDetectTool(), detectToolProjectInfo1.getSuggestedNameVersion()));
        detectableToolResult.getDockerTar().ifPresent(this::addDockerTargetData);
        detectCodeLocations.addAll(detectableToolResult.getDetectCodeLocations());
        anyFailed = detectableToolResult.isFailure() || anyFailed;
    }

    public void addDetectorToolResult(DetectorToolResult detectorToolResult) {
        detectorToolResult.getBomToolProjectNameVersion().ifPresent(it -> addToolNameVersion(DetectTool.DETECTOR, new NameVersion(it.getName(), it.getVersion())));
        detectCodeLocations.addAll(detectorToolResult.getBomToolCodeLocations());
        anyFailed = detectorToolResult.anyDetectorsFailed() || anyFailed;
    }

    public void addDockerTargetData(final DockerTargetData dockerTargetData) {
        this.dockerTargetData = dockerTargetData;
    }

    public UniversalToolsResult build() {
        return new UniversalToolsResult(anyFailed, dockerTargetData, detectToolProjectInfo, detectCodeLocations);
    }

    public void addToolNameVersion(final DetectTool detectTool, final NameVersion toolNameVersion) {
        final DetectToolProjectInfo dockerProjectInfo = new DetectToolProjectInfo(detectTool, new NameVersion(toolNameVersion.getName(), toolNameVersion.getVersion()));
        detectToolProjectInfo.add(dockerProjectInfo);
    }

}
