package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class FinalStepTransformColonSeparatedGavsToMaven implements FinalStep {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public FinalStepTransformColonSeparatedGavsToMaven(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    @Override
    public List<Dependency> finish(List<String> gavStrings) throws DetectableException {
        List<Dependency> dependencies = new ArrayList<>();
        for (String gavString : gavStrings) {
            Dependency artifactDependency = gavStringToDependency(gavString, ":");
            try {
                dependencies.add(artifactDependency);
            } catch (Exception e) {
                logger.error(String.format("Unable to create dependency from %s", gavString));
            }
        }
        return dependencies;
    }

    private Dependency gavStringToDependency(String artifactString, String separatorRegex) {
        String[] gavParts = artifactString.split(separatorRegex);
        String group = gavParts[0];
        String artifact = gavParts[1];
        String version = gavParts[gavParts.length - 1];

        logger.debug("Adding dependency from external id: {}:{}:{}", group, artifact, version);
        ExternalId externalId = externalIdFactory.createMavenExternalId(group, artifact, version);
        return new Dependency(artifact, version, externalId);
    }
}
