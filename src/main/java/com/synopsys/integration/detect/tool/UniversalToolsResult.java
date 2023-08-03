package com.synopsys.integration.detect.tool;

import java.util.List;
import java.util.Set;

import com.synopsys.integration.blackduck.bdio2.model.GitInfo;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.project.DetectToolProjectInfo;
import com.synopsys.integration.detector.base.DetectorType;

public class UniversalToolsResult {
    private final boolean anyFailed;
    private final DockerTargetData dockerTargetData;
    private final GitInfo detectToolGitInfo;
    private final List<DetectToolProjectInfo> detectToolProjectInfo;
    private final List<DetectCodeLocation> detectCodeLocations;
    private final Set<DetectorType> applicableDetectorTypes;

    public UniversalToolsResult(
        boolean anyFailed,
        DockerTargetData dockerTargetData,
        GitInfo detectToolGitInfo,
        List<DetectToolProjectInfo> detectToolProjectInfo,
        List<DetectCodeLocation> detectCodeLocations,
        Set<DetectorType> applicableDetectorTypes
    ) {
        this.anyFailed = anyFailed;
        this.dockerTargetData = dockerTargetData;
        this.detectToolGitInfo = detectToolGitInfo;
        this.detectToolProjectInfo = detectToolProjectInfo;
        this.detectCodeLocations = detectCodeLocations;
        this.applicableDetectorTypes = applicableDetectorTypes;
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

    public Set<DetectorType> getApplicableDetectorTypes() {
        return applicableDetectorTypes;
    }
}
