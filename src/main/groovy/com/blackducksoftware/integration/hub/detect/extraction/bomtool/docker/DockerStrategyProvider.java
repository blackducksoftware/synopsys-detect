package com.blackducksoftware.integration.hub.detect.extraction.bomtool.docker;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.DockerInspectorRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.StandardExecutableRequirement.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyProvider;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class DockerStrategyProvider extends StrategyProvider {

    @Autowired
    protected DetectConfiguration detectConfiguration;


    @SuppressWarnings("rawtypes")
    @Override
    public List<Strategy> createStrategies() {

        final Strategy imageStrategy = newStrategyBuilder(DockerContext.class, DockerExtractor.class)
                .needsBomTool(BomToolType.DOCKER).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsString(detectConfiguration.getDockerImage()).as((context, value) -> context.image = value)
                .demandsStandardExecutable(StandardExecutableType.BASH).as((context, file) -> context.bashExe = file)
                .demandsStandardExecutable(StandardExecutableType.DOCKER).as((context, file) -> context.dockerExe = file)
                .demands(new DockerInspectorRequirement(), (context, info) -> context.dockerInspectorInfo = info)
                .build();

        final Strategy tarStrategy = newStrategyBuilder(DockerContext.class, DockerExtractor.class)
                .needsBomTool(BomToolType.DOCKER).noop()
                .needsCurrentDirectory((context, file) -> context.directory = file)
                .needsString(detectConfiguration.getDockerTar()).as((context, value) -> context.tar = value)
                .demandsStandardExecutable(StandardExecutableType.BASH).as((context, file) -> context.bashExe = file)
                .demandsStandardExecutable(StandardExecutableType.DOCKER).as((context, file) -> context.dockerExe = file)
                .demands(new DockerInspectorRequirement(), (context, info) -> context.dockerInspectorInfo = info)
                .build();

        return Arrays.asList(imageStrategy, tarStrategy);

    }

}
