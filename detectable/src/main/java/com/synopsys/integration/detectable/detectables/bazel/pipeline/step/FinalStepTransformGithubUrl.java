package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class FinalStepTransformGithubUrl implements FinalStep {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public FinalStepTransformGithubUrl(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    @Override
    public List<Dependency> finish(List<String> input) throws DetectableException {
        for (String githubUrl : input) {
            logger.info("githubUrl: {}", githubUrl);
        }
        return new ArrayList<>();
    }
}
