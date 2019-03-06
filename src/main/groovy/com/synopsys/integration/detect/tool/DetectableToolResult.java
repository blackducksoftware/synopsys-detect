package com.synopsys.integration.detect.tool;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.project.DetectToolProjectInfo;
import com.synopsys.integration.detect.workflow.project.DetectorProjectInfo;

public class DetectableToolResult {
    private Optional<File> dockerTar = Optional.empty();
    private final Optional<DetectToolProjectInfo> detectToolProjectInfo;
    private final List<DetectCodeLocation> detectCodeLocations;

    public DetectableToolResult(final Optional<DetectToolProjectInfo> detectToolProjectInfo, final List<DetectCodeLocation> detectCodeLocations, Optional<File> dockerTar) {
        this.detectToolProjectInfo = detectToolProjectInfo;
        this.detectCodeLocations = detectCodeLocations;
        this.dockerTar = dockerTar;
    }

    public static DetectableToolResult skip() {
        return new DetectableToolResult(Optional.empty(), Collections.emptyList(), Optional.empty());
    }

    public static DetectableToolResult complete(List<DetectCodeLocation> codeLocations, DetectToolProjectInfo projectInfo, File dockerTar) {
        return new DetectableToolResult(Optional.of(projectInfo), codeLocations, Optional.of(dockerTar));
    }
}
