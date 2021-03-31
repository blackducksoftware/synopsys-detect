/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.tool.DetectableToolResult;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.project.DetectToolProjectInfo;
import com.synopsys.integration.util.NameVersion;

public class RunResult {
    private DockerTargetData dockerTargetData = null;
    private final List<DetectToolProjectInfo> detectToolProjectInfo = new ArrayList<>();
    private final List<DetectCodeLocation> detectCodeLocations = new ArrayList<>();

    public void addToolNameVersion(final DetectTool detectTool, final NameVersion toolNameVersion) {
        final DetectToolProjectInfo dockerProjectInfo = new DetectToolProjectInfo(detectTool, new NameVersion(toolNameVersion.getName(), toolNameVersion.getVersion()));
        detectToolProjectInfo.add(dockerProjectInfo);
    }

    public void addDetectCodeLocations(final List<DetectCodeLocation> codeLocations) {
        detectCodeLocations.addAll(codeLocations);
    }

    public void addDetectableToolResult(final DetectableToolResult detectableToolResult) {
        detectableToolResult.getDetectToolProjectInfo().ifPresent(detectToolProjectInfo1 -> addToolNameVersion(detectToolProjectInfo1.getDetectTool(), detectToolProjectInfo1.getSuggestedNameVersion()));
        detectableToolResult.getDockerTar().ifPresent(this::addDockerTargetData);
        detectCodeLocations.addAll(detectableToolResult.getDetectCodeLocations());
    }

    public void addDockerTargetData(final DockerTargetData dockerTargetData) {
        this.dockerTargetData = dockerTargetData;
    }

    public Optional<DockerTargetData> getDockerTargetData() {
        return Optional.ofNullable(dockerTargetData);
    }

    public List<DetectToolProjectInfo> getDetectToolProjectInfo() {
        return detectToolProjectInfo;
    }

    public List<DetectCodeLocation> getDetectCodeLocations() {
        return detectCodeLocations;
    }
}
