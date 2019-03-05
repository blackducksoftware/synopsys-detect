package com.synopsys.integration.detect.workflow.codelocation;

import java.io.File;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detector.base.DetectorType;

public class DetectCodeLocation {
    private final DependencyGraph dependencyGraph;
    private final File sourcePath;
    private final ExternalId externalId;
    private final String creatorName;
    private final String dockerImageName;

    private DetectCodeLocation(final DependencyGraph dependencyGraph, final File sourcePath, final ExternalId externalId, final String creatorName,
        final String dockerImageName) {
        this.dependencyGraph = dependencyGraph;
        this.sourcePath = sourcePath;
        this.externalId = externalId;
        this.creatorName = creatorName;
        this.dockerImageName = dockerImageName;

        if (StringUtils.isNotBlank(dockerImageName) && StringUtils.isNotBlank(creatorName)){
            throw new IllegalArgumentException("Detect code location cannot have the Docker image name and the creator name set as the docker image name will means no creator exists.");
        }
    }

    public static DetectCodeLocation forDocker(DependencyGraph dependencyGraph, File sourcePath, ExternalId externalId, String dockerImageName){
        return new DetectCodeLocation(dependencyGraph, sourcePath, externalId, null, dockerImageName);
    }

    public static DetectCodeLocation forDetector(DependencyGraph dependencyGraph, File sourcePath, ExternalId externalId, DetectorType detectorType){
        return new DetectCodeLocation(dependencyGraph, sourcePath, externalId, detectorType.toString(), null);
    }

    public static DetectCodeLocation forCreator(DependencyGraph dependencyGraph, File sourcePath, ExternalId externalId, String creatorName){
        return new DetectCodeLocation(dependencyGraph, sourcePath, externalId, creatorName, null);
    }

    public DetectCodeLocation copy(DependencyGraph dependencyGraph){
        return new DetectCodeLocation(dependencyGraph, sourcePath, externalId, creatorName, dockerImageName);
    }

    public Optional<String> getCreatorName() {
        return Optional.ofNullable(creatorName);
    }

    public Optional<String> getDockerImageName() {
        return Optional.ofNullable(dockerImageName);
    }

    public DependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }

    public File getSourcePath() {
        return sourcePath;
    }

    public ExternalId getExternalId() {
        return externalId;
    }
}
