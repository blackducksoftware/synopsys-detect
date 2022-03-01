package com.synopsys.integration.detect.tool;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.project.DetectToolProjectInfo;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;

public class DetectableToolResult {
    private enum DetectableToolResultType {
        SKIPPED,
        FAILED,
        SUCCESS
    }

    private final DetectableToolResultType resultType;
    private final List<DetectCodeLocation> detectCodeLocations;

    @Nullable
    private final DockerTargetData dockerTargetData;
    @Nullable
    private final DetectToolProjectInfo detectToolProjectInfo;
    @Nullable
    private final DetectableResult failedExtractableResult;

    public DetectableToolResult(
        DetectableToolResultType resultType,
        @Nullable DetectToolProjectInfo detectToolProjectInfo,
        List<DetectCodeLocation> detectCodeLocations,
        @Nullable DockerTargetData dockerTargetData,
        @Nullable DetectableResult failedExtractableResult
    ) {
        this.resultType = resultType;
        this.detectToolProjectInfo = detectToolProjectInfo;
        this.detectCodeLocations = detectCodeLocations;
        this.dockerTargetData = dockerTargetData;
        this.failedExtractableResult = failedExtractableResult;
    }

    public static DetectableToolResult skip() {
        return new DetectableToolResult(DetectableToolResultType.SKIPPED, null, Collections.emptyList(), null, null);
    }

    //extractableResult is a workaround for docker. Technically we want to throw an exception when docker extractable fails for Windows but neither the tool nor the detectable supports that.
    //This allows the caller to make determinations based on the extractable result.
    public static DetectableToolResult failed(DetectableResult extractableResult) {
        return new DetectableToolResult(DetectableToolResultType.FAILED, null, Collections.emptyList(), null, extractableResult);
    }

    public static DetectableToolResult failed() {
        return failed(null);
    }

    public static DetectableToolResult success(List<DetectCodeLocation> codeLocations, @Nullable DetectToolProjectInfo projectInfo, @Nullable DockerTargetData dockerTargetData) {
        return new DetectableToolResult(DetectableToolResultType.SUCCESS, projectInfo, codeLocations, dockerTargetData, null);
    }

    public Optional<DockerTargetData> getDockerTar() {
        return Optional.ofNullable(dockerTargetData);
    }

    public Optional<DetectToolProjectInfo> getDetectToolProjectInfo() {
        return Optional.ofNullable(detectToolProjectInfo);
    }

    public List<DetectCodeLocation> getDetectCodeLocations() {
        return detectCodeLocations;
    }

    public boolean isFailure() {
        return resultType == DetectableToolResultType.FAILED;
    }

    public Optional<DetectableResult> getFailedExtractableResult() {
        return Optional.ofNullable(failedExtractableResult);
    }
}
