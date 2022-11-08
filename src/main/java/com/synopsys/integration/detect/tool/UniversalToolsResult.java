package com.synopsys.integration.detect.tool;

import java.util.List;

import com.synopsys.integration.blackduck.bdio2.model.GitInfo;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.project.DetectToolProjectInfo;

public class UniversalToolsResult {
    private final boolean anyFailed;
    private final DockerTargetData dockerTargetData;
    private final GitInfo detectToolGitInfo;
    private final List<DetectToolProjectInfo> detectToolProjectInfo;
    private final List<DetectCodeLocation> detectCodeLocations;

    public UniversalToolsResult(
        boolean anyFailed,
        DockerTargetData dockerTargetData,
        GitInfo detectToolGitInfo,
        List<DetectToolProjectInfo> detectToolProjectInfo,
        List<DetectCodeLocation> detectCodeLocations
    ) {
        this.anyFailed = anyFailed;
        this.dockerTargetData = dockerTargetData;
        this.detectToolGitInfo = detectToolGitInfo;
        this.detectToolProjectInfo = detectToolProjectInfo;
        this.detectCodeLocations = detectCodeLocations;
    }

    public boolean didAnyFail() {
        return anyFailed;
    }

    public GitInfo getDetectToolGitInfo() {
        return detectToolGitInfo;
    }

    public DockerTargetData getDockerTargetData() {
        return dockerTargetData;
    }

    public List<DetectToolProjectInfo> getDetectToolProjectInfo() {
        return detectToolProjectInfo;
    }

    public List<DetectCodeLocation> getDetectCodeLocations() {
        return detectCodeLocations;
    }
}
