package com.synopsys.integration.detectable.detectables.docker;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.PassedResultBuilder;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.DockerResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.JavaResolver;
import com.synopsys.integration.detectable.detectable.explanation.PropertyProvided;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.InspectorNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PropertyInsufficientDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.executable.ExecutableRunnerException;

//TODO: Violates Detectable contract. Take folder -> give project data. This does not take a folder.
@DetectableInfo(name = "Docker CLI", language = "N/A", forge = "Derived from the Linux distribution", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "Access to a Docker Engine. See <a href='https://blackducksoftware.github.io/blackduck-docker-inspector/latest/overview/'>Docker Inspector documentation</a> for details.")
public class DockerDetectable extends Detectable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DockerInspectorResolver dockerInspectorResolver;
    private final JavaResolver javaResolver;
    private final DockerResolver dockerResolver;
    private final DockerExtractor dockerExtractor;
    private final DockerDetectableOptions dockerDetectableOptions;

    private ExecutableTarget javaExe;
    private ExecutableTarget dockerExe;
    private DockerInspectorInfo dockerInspectorInfo;

    public DockerDetectable(
        DetectableEnvironment environment,
        DockerInspectorResolver dockerInspectorResolver,
        JavaResolver javaResolver,
        DockerResolver dockerResolver,
        DockerExtractor dockerExtractor,
        DockerDetectableOptions dockerDetectableOptions
    ) {
        super(environment);
        this.javaResolver = javaResolver;
        this.dockerResolver = dockerResolver;
        this.dockerExtractor = dockerExtractor;
        this.dockerInspectorResolver = dockerInspectorResolver;
        this.dockerDetectableOptions = dockerDetectableOptions;
    }

    @Override
    public DetectableResult applicable() {
        if (!dockerDetectableOptions.hasDockerImageOrTar()) {
            return new PropertyInsufficientDetectableResult();
        }
        return new PassedDetectableResult(new PropertyProvided("docker image or tar"));
    }

    @Override
    public DetectableResult extractable() throws DetectableException { //TODO: Can this be improved with a Requirements object? - jp
        PassedResultBuilder passedResultBuilder = new PassedResultBuilder();
        javaExe = javaResolver.resolveJava();
        if (javaExe == null) {
            return new ExecutableNotFoundDetectableResult("java");
        } else {
            passedResultBuilder.foundExecutable(javaExe);
        }
        try {
            dockerExe = dockerResolver.resolveDocker();
        } catch (Exception e) {
            dockerExe = null;
        }
        if (dockerExe != null) {
            passedResultBuilder.foundExecutable(dockerExe);
        }
        dockerInspectorInfo = dockerInspectorResolver.resolveDockerInspector();
        if (dockerInspectorInfo == null) {
            return new InspectorNotFoundDetectableResult("docker");
        } else {
            passedResultBuilder.foundInspector(dockerInspectorInfo.getDockerInspectorJar());
        }
        return passedResultBuilder.build();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws IOException, ExecutableRunnerException {
        String image = dockerDetectableOptions.getSuppliedDockerImage().orElse("");
        String imageId = dockerDetectableOptions.getSuppliedDockerImageId().orElse("");
        String tar = dockerDetectableOptions.getSuppliedDockerTar().orElse("");
        return dockerExtractor.extract(environment.getDirectory(), extractionEnvironment.getOutputDirectory(), dockerExe, javaExe, image, imageId, tar, dockerInspectorInfo,
            new DockerProperties(dockerDetectableOptions)
        ); //TODO, doesn't feel right to construct properties here. -jp
    }
}

