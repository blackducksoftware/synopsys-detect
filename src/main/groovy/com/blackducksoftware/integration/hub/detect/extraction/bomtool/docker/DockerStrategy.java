package com.blackducksoftware.integration.hub.detect.extraction.bomtool.docker;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.Applicable;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class DockerStrategy extends Strategy<DockerContext, DockerExtractor> {
    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public DockerInspectorManager dockerInspectorManager;

    @Autowired
    public StandardExecutableFinder standardExecutableFinder;

    public DockerStrategy() {
        super("Docker", BomToolType.DOCKER, DockerContext.class, DockerExtractor.class);
    }

    public Applicable applicable(final EvaluationContext evaluation, final DockerContext context) {
        context.image = detectConfiguration.getDockerImage();
        context.tar = detectConfiguration.getDockerTar();

        if (StringUtils.isBlank(context.image) && StringUtils.isBlank(context.tar)) {
            return Applicable.doesNotApply("Docker properties were not sufficient to run.");
        }

        return Applicable.doesApply();
    }

    public Extractable extractable(final EvaluationContext evaluation, final DockerContext context){
        context.bashExe = standardExecutableFinder.getExecutable(StandardExecutableType.BASH);
        if (context.bashExe == null) {
            return Extractable.canNotExtract("No bash executable was found.");
        }

        context.dockerExe = standardExecutableFinder.getExecutable(StandardExecutableType.DOCKER);
        if (context.dockerExe == null) {
            return Extractable.canNotExtract("No docker executable was found.");
        }

        context.dockerInspectorInfo = dockerInspectorManager.getDockerInspector(evaluation);
        if (context.dockerInspectorInfo == null) {
            return Extractable.canNotExtract("No docker inspector was found.");
        }

        return Extractable.canExtract();
    }

}