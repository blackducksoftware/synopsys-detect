package com.synopsys.integration.detect.workflow.codelocation;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detector.base.DetectorType;

public class DetectCodeLocation {
    private final CodeLocation codeLocation;
    private final String creatorName;
    private final String dockerImageName;

    private DetectCodeLocation(final CodeLocation codeLocation, final String creatorName, final String dockerImageName) {
        this.codeLocation = codeLocation;
        this.creatorName = creatorName;
        this.dockerImageName = dockerImageName;

        if (StringUtils.isNotBlank(dockerImageName) && StringUtils.isNotBlank(creatorName)){
            throw new IllegalArgumentException("Detect code location cannot have the Docker image name and the creator name set as the docker image name will means no creator exists.");
        }
    }

    public static DetectCodeLocation forDocker(CodeLocation codeLocation, String dockerImageName){
        return new DetectCodeLocation(codeLocation, null, dockerImageName);
    }

    public static DetectCodeLocation forDetector(CodeLocation codeLocation, DetectorType detectorType){
        return new DetectCodeLocation(codeLocation, detectorType.toString(), null);
    }

    public static DetectCodeLocation forCreator(CodeLocation codeLocation, String creatorName){
        return new DetectCodeLocation(codeLocation, creatorName, null);
    }

    public CodeLocation getCodeLocation() {
        return codeLocation;
    }

    public Optional<String> getCreatorName() {
        return Optional.ofNullable(creatorName);
    }

    public Optional<String> getDockerImageName() {
        return Optional.ofNullable(dockerImageName);
    }
}
