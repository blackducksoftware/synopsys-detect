package com.blackducksoftware.integration.hub.detect.lifecycle.run;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.blackducksoftware.integration.hub.detect.DetectTool;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.project.DetectToolProjectInfo;
import com.synopsys.integration.util.NameVersion;

public class RunResult {

    private Optional<File> dockerTar = Optional.empty();
    private final List<DetectToolProjectInfo> detectToolProjectInfo = new ArrayList<>();
    private final List<DetectCodeLocation> detectCodeLocations = new ArrayList<>();

    public void addToolNameVersionIfPresent(DetectTool detectTool, Optional<NameVersion> toolNameVersion) {
        if (toolNameVersion.isPresent()) {
            DetectToolProjectInfo dockerProjectInfo = new DetectToolProjectInfo(detectTool, new NameVersion(toolNameVersion.get().getName(), toolNameVersion.get().getVersion()));
            detectToolProjectInfo.add(dockerProjectInfo);
        }
    }

    public void addDetectCodeLocations(List<DetectCodeLocation> codeLocations) {
        detectCodeLocations.addAll(codeLocations);
    }

    public void addDockerFile(Optional<File> dockerFile) {
        dockerTar = dockerFile;
    }

    public Optional<File> getDockerTar() {
        return dockerTar;
    }

    public List<DetectToolProjectInfo> getDetectToolProjectInfo() {
        return detectToolProjectInfo;
    }

    public List<DetectCodeLocation> getDetectCodeLocations() {
        return detectCodeLocations;
    }

}
