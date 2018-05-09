package com.blackducksoftware.integration.hub.detect.extraction.bomtool.docker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.DockerInspectorRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.StandardExecutableRequirement.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@Component
public class DockerStrategyProvider extends StrategyProvider {

    @Autowired
    protected DetectConfiguration detectConfiguration;


    @SuppressWarnings("rawtypes")
    @Override
    public void init() {

        final Strategy imageStrategy = newStrategyBuilder(DockerContext.class, DockerExtractor.class)
                .named("Docker Image", BomToolType.DOCKER)
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsString(detectConfiguration.getDockerImage()).failWith("No Docker image was provided.").as((context, value) -> context.image = value)
                .demandsStandardExecutable(StandardExecutableType.BASH).as((context, file) -> context.bashExe = file)
                .demandsStandardExecutable(StandardExecutableType.DOCKER).as((context, file) -> context.dockerExe = file)
                .demands(new DockerInspectorRequirement(), (context, info) -> context.dockerInspectorInfo = info)
                .maxDepth(0)
                .build();

        final Strategy tarStrategy = newStrategyBuilder(DockerContext.class, DockerExtractor.class)
                .named("Docker Tar", BomToolType.DOCKER)
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsString(detectConfiguration.getDockerTar()).failWith("No Docker tar was provided.").as((context, value) -> context.tar = value)
                .demandsStandardExecutable(StandardExecutableType.BASH).as((context, file) -> context.bashExe = file)
                .demandsStandardExecutable(StandardExecutableType.DOCKER).as((context, file) -> context.dockerExe = file)
                .demands(new DockerInspectorRequirement(), (context, info) -> context.dockerInspectorInfo = info)
                .maxDepth(0)
                .build();

        add(imageStrategy, tarStrategy);

    }

}
