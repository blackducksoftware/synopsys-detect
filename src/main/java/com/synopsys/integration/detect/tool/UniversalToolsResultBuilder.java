package com.synopsys.integration.detect.tool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.synopsys.integration.blackduck.bdio2.model.GitInfo;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.run.data.DockerTargetData;
import com.synopsys.integration.detect.tool.detector.DetectorToolResult;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.project.DetectToolProjectInfo;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.util.NameVersion;

public class UniversalToolsResultBuilder {
    private DockerTargetData dockerTargetData = null;
    private GitInfo detectToolGitInfo = GitInfo.none();
    private final List<DetectToolProjectInfo> detectToolProjectInfo = new ArrayList<>();
    private final List<DetectCodeLocation> detectCodeLocations = new ArrayList<>();
    private boolean anyFailed = false;
    private final Set<DetectorType> applicableDetectorTypes = new HashSet<>();

    public void addDetectableToolResult(DetectableToolResult detectableToolResult) {
        detectableToolResult.getDetectToolProjectInfo()
            .ifPresent(detectToolProjectInfo1 -> addToolNameVersion(detectToolProjectInfo1.getDetectTool(), detectToolProjectInfo1.getSuggestedNameVersion()));
        detectableToolResult.getDockerTar().ifPresent(this::addDockerTargetData);
        detectCodeLocations.addAll(detectableToolResult.getDetectCodeLocations());
        anyFailed = detectableToolResult.isFailure() || anyFailed;
    }

    public void addDetectorToolResult(DetectorToolResult detectorToolResult) {
        detectorToolResult.getBomToolProjectNameVersion().ifPresent(it -> addToolNameVersion(DetectTool.DETECTOR, new NameVersion(it.getName(), it.getVersion())));
        detectCodeLocations.addAll(detectorToolResult.getBomToolCodeLocations());
        detectToolGitInfo = detectorToolResult.getGitInfo();
        anyFailed = detectorToolResult.anyDetectorsFailed() || anyFailed;
        applicableDetectorTypes.addAll(detectorToolResult.getApplicableDetectorTypes());
    }

    public UniversalToolsResult build() {
        return new UniversalToolsResult(anyFailed, dockerTargetData, detectToolGitInfo, detectToolProjectInfo, detectCodeLocations, applicableDetectorTypes);
    }

    private void addDockerTargetData(DockerTargetData dockerTargetData) {
        this.dockerTargetData = dockerTargetData;
    }

    private void addToolNameVersion(DetectTool detectTool, NameVersion toolNameVersion) {
        DetectToolProjectInfo dockerProjectInfo = new DetectToolProjectInfo(detectTool, new NameVersion(toolNameVersion.getName(), toolNameVersion.getVersion()));
        detectToolProjectInfo.add(dockerProjectInfo);
    }

}
